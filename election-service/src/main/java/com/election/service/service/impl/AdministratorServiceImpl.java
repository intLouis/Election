package com.election.service.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.election.api.dto.administrator.*;
import com.election.api.enums.CacheKey;
import com.election.api.enums.ElectionState;
import com.election.service.bo.CandidateBO;
import com.election.service.bo.ElectionBO;
import com.election.service.bo.ElectionRecordBO;
import com.election.service.bo.ElectionResultBO;
import com.election.service.bo.manager.CandidateManager;
import com.election.service.bo.manager.ElectionManager;
import com.election.service.bo.manager.ElectionRecordManager;
import com.election.service.bo.manager.ElectionResultManager;
import com.election.service.context.UserContext;
import com.election.service.mapstruct.CandidateConverter;
import com.election.service.mapstruct.ElectionConverter;
import com.election.service.mapstruct.ElectionRecordConverter;
import com.election.service.mapstruct.ElectionResultConverter;
import com.election.service.mq.consumer.VoteMessageConsumer;
import com.election.service.query.ElectionRecordQuery;
import com.election.service.schedule.FlushVoteInfoToDBTask;
import com.election.service.service.AdministratorService;
import com.election.service.utils.EmailTextEnum;
import com.election.service.utils.IdNumberCheck;
import com.election.service.utils.MsgEnum;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.election.api.enums.CacheKey.getBizCacheKey;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorServiceImpl implements AdministratorService {

    private final CandidateManager candidateManager;
    private final ElectionManager electionManager;
    private final ElectionResultManager electionResultManager;
    private final ElectionRecordManager electionRecordManager;
    private final CandidateConverter candidateConverter;
    private final ElectionConverter electionConverter;
    private final ElectionResultConverter electionResultConverter;
    private final ElectionRecordConverter electionRecordConverter;
    private final MailAccount mailAccount;
    private final VoteMessageConsumer voteMessageConsumer;
    private final FlushVoteInfoToDBTask flushVoteInfoToDBTask;
    private final TransactionTemplate transactionTemplate;
    private final Redisson redisson;
    private final UserContext userContext;

    @Override
    public void addCandidate(final AddCandidateDTO.@NonNull Req req) {
        final var addCandidate = candidateConverter.to(req);

        final var electionResultBO = candidateConverter.toElectionResult(addCandidate);

        //?????????????????????????????????????????????????????????????????????
        final var addLock = redisson.getLock(getBizCacheKey(CacheKey.ELECTION, req.getElectionId()));
        addLock.lock();
        try {
            //??????????????????
            check(req);
            transactionTemplate.executeWithoutResult(e -> {
                //?????????????????????
                final var candidateId = candidateManager.add(addCandidate);
                //????????????????????????
                electionResultManager.add(electionResultBO, candidateId);
            });
        } finally {
            addLock.unlock();
        }
    }

    private void check(@NonNull final AddCandidateDTO.Req req) {

        final var electionBO = electionManager.getByElectionId(req.getElectionId());
        final var isOk = !Objects.isNull(electionBO) && Objects.equals(electionBO.getState(), ElectionState.STANDBY);

        Assert.isTrue(isOk, () -> {
            log.error("??????????????????????????????????????????????????????????????? param???{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        //??????????????????
        final var idNumber = req.getIdNumber();
        final var validHKCard = IdNumberCheck.checkHKIdNumber(idNumber);
        Assert.isTrue(validHKCard, () -> {
            log.error("???????????????????????? param???{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        //?????????????????????
        final var isNoNull = Objects.isNull(candidateManager.getByIdNumber(idNumber));
        Assert.isTrue(isNoNull, () -> {
            log.error("?????????????????? param???{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });


    }


    @Override
    public GetVoterInfoDTO.Rsp getVoterInfo(@NonNull final GetVoterInfoDTO.Req req) {

        final var getElectionRecordByPageQuery = electionRecordConverter.to(req);
        final var electionRecordByPage = electionRecordManager.getElectionRecordByPage(getElectionRecordByPageQuery);

        if (CollectionUtil.isEmpty(electionRecordByPage)) return null;

        final var anchor = Objects.toString(electionRecordByPage.get(electionRecordByPage.size() - 1).getId());
        final var collect = electionRecordByPage.stream()
                .map(electionRecordConverter::to)
                .collect(Collectors.toList());

        return GetVoterInfoDTO.Rsp.builder()
                .electionRecords(collect)
                .anchor(anchor)
                .build();

    }


    @Override
    public void addElection(@NonNull final AddElectionDTO.Req req) {

        electionManager.add(electionConverter.to(req));
    }


    @SneakyThrows
    @Override
    public void changeState(StartOrEndElectionDTO.@NonNull Req req) {
        //????????????
        final var state = req.getState();
        final var electionId = req.getElectionId();
        Assert.isTrue(ElectionState.startOrEnd(state), () -> {
            log.error("???????????????????????????????????????????????????????????? electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //??????????????????????????????????????????
        final var startLock = redisson.getLock(getBizCacheKey(CacheKey.ELECTION, electionId));
        try {
            final var b = startLock.tryLock(5, TimeUnit.SECONDS);
            //??????????????????
            Assert.isTrue(b, () -> {
                log.error("???????????????????????? req:{}", req);
                return MsgEnum.BAD_REQUEST.desc;
            });

            final var electionBO = electionManager.getByElectionId(electionId);
            switch (state) {
                case START -> startElection(electionBO);
                case END -> stopElection(electionBO);
            }

            //????????????
            electionManager.changeState(electionBO);

        } finally {
            startLock.unlock();
        }
    }


    private void startElection(ElectionBO electionBO) {
        final var electionId = electionBO.getElectionId();

        //???????????????????????????????????????
        final List<CandidateBO> candidateBOS = checkStartElection(electionBO, electionId);

        electionBO.setState(ElectionState.START);
        //????????????
        toSetCache(electionBO, candidateBOS);

        //????????????????????????
        CompletableFuture.runAsync(() -> {
            try {
                voteMessageConsumer.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @NonNull
    private List<CandidateBO> checkStartElection(ElectionBO electionBO, String electionId) {
        Assert.isTrue(Objects.equals(electionBO.getState(), ElectionState.STANDBY), () -> {
            log.error("??????????????????????????????");
            return MsgEnum.BAD_REQUEST.desc;
        });

        final var candidateBOS = candidateManager.getByElectionId(electionId);

        Assert.isTrue(candidateBOS.size() >= 2, () -> {
            log.error("????????????????????????2???????????????????????????electionId:{}", electionId);
            return MsgEnum.BUSINESS_LOGIC_ERROR.desc;
        });
        return candidateBOS;
    }


    private void toSetCache(final ElectionBO electionBO, final @NotEmpty List<CandidateBO> candidateBOS) {
        final var electionId = electionBO.getElectionId();

        //????????????????????????
        final RMap<String, CandidateBO> inProgressCacheMap = redisson.getMap(CacheKey.getInProgressCandidateCacheKey(electionId));
        //?????????????????????
        final RMap<String, Long> candidateVotesCacheMap = redisson.getMap(CacheKey.getCandidateVotesCacheKey(electionId), StringCodec.INSTANCE);
        //???????????????
        final RMap<String, ElectionBO> inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //????????????????????????
        final RBloomFilter<String> bloomFilter = redisson.getBloomFilter(CacheKey.getBizCacheKey(CacheKey.VOTEBLOOMFILTER, electionId));
        bloomFilter.tryInit(1000, 0.01);

        //???????????????????????????
        final var newestElectionBO = electionManager.getByElectionId(electionId);
        inProgressElectionCacheMap.put(electionId, newestElectionBO);

        //????????????
        candidateBOS.forEach(item -> {
            inProgressCacheMap.put(item.getCandidateId(), item);
            candidateVotesCacheMap.put(item.getCandidateId(), 0L);
        });


    }

    private void stopElection(final @NonNull ElectionBO electionBO) {
        final var electionId = electionBO.getElectionId();
        //????????????????????????????????????
        Assert.isTrue(Objects.equals(electionBO.getState(), ElectionState.START), () -> {
            log.error("??????????????????????????? electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //????????????
        deleteCache(electionId);

        //????????????????????????????????????????????????db
        CompletableFuture.runAsync(() -> flushVoteInfoToDBTask.executeTask(electionId));

        //??????????????????
        CompletableFuture.runAsync(() -> sendEmail(electionBO));

        electionBO.setState(ElectionState.END);
    }


    private void sendEmail(@NonNull final ElectionBO electionBO) {
        final var electionId = electionBO.getElectionId();

        String anchor = "";
        while (true) {
            final var electionRecordByPage = electionRecordManager.getElectionRecordByPage(
                    ElectionRecordQuery.GetElectionRecordByPageQuery.builder()
                            .electionId(electionId)
                            .anchor(anchor)
                            .pageSize(100)
                            .build()
            );
            if (CollectionUtil.isEmpty(electionRecordByPage)) break;

            electionRecordByPage.forEach(this::doSend);
            //??????????????????
            anchor = Objects.toString(electionRecordByPage.get(electionRecordByPage.size() - 1).getId());
        }
    }

    private void doSend(@NonNull final ElectionRecordBO electionRecordBO) {
        final var electionId = electionRecordBO.getElectionId();
        final var electionBO = electionManager.getByElectionId(electionId);

        //?????????????????????
        final var electionResultBOS = electionResultManager.getByElectionId(electionId)
                .stream()
                .collect(Collectors.toMap(ElectionResultBO::getCandidateId, Function.identity()));

        final var candidateBOS = candidateManager.getByElectionId(electionId);
        //????????????
        final var content = buildResultContent(electionResultBOS, candidateBOS);
        //?????????
        final String title = EmailTextEnum.getTitleText(EmailTextEnum.SEND_RESULT_CONTENT_TEXT, electionBO.getName());

        try {
            //????????????
            MailUtil.send(mailAccount, electionRecordBO.getVoterEmail(), title, content, false);
            //????????????????????????
            electionRecordManager.updateById(electionRecordConverter.toUpdateQuery(electionRecordBO));

            log.info("?????????????????? param:{}", electionRecordBO);
        } catch (Exception e) {
            log.warn("?????????????????? voterEmail:{}", electionRecordBO.getVoterEmail());
            e.printStackTrace();
        }


    }

    private String buildResultContent(Map<String, ElectionResultBO> electionResultBOS, List<CandidateBO> candidateBOS) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < candidateBOS.size(); i++) {
            final var candidateBO = candidateBOS.get(i);
            final var electionResultBO = electionResultBOS.get(candidateBO.getCandidateId());
            content.append(candidateBO.getName().concat(":")).append(electionResultBO.getVotes().toString()).append("\n");
        }
        return content.toString();
    }

    @SneakyThrows
    private void deleteCache(@NotBlank final String electionId) {

        //????????????????????????????????????????????????
        final var lock = redisson.getLock(CacheKey.FLUSHVOTESTODBINPROGRESS.code);
        final boolean tryLock;
        tryLock = lock.tryLock(5, TimeUnit.SECONDS);

        Assert.isTrue(tryLock, () -> {
            log.warn("???????????????????????????????????????????????????????????? electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //????????????????????????
        final var inProgressCacheMap = redisson.getMap(CacheKey.getInProgressCandidateCacheKey(electionId));
        //?????????????????????
        final var candidateVotesCacheMap = redisson.getMap(CacheKey.getCandidateVotesCacheKey(electionId), new StringCodec());
        //???????????????
        final var inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //???????????????????????????
        final var bloomFilter = redisson.getBloomFilter(CacheKey.getBizCacheKey(CacheKey.VOTEBLOOMFILTER, electionId));
        bloomFilter.delete();
        inProgressCacheMap.delete();
        candidateVotesCacheMap.delete();
        inProgressElectionCacheMap.delete();

        lock.unlock();
    }


    @Override
    public GetElectionResultDTO.Rsp electionResult(final @NonNull GetElectionResultDTO.Req req) {

        final var electionId = req.getElectionId();

        //??????????????????
        final var electionBO = check(req, electionId);

        //????????????

        //????????????
        final var electionResultBOS = electionResultManager.getByElectionId(electionId);
        //???????????????
        final var candidateBOSMap = candidateManager.getByElectionId(electionId)
                .stream()
                .collect(Collectors.toMap(CandidateBO::getCandidateId, Function.identity()));


        final var electionResults = electionResultBOS.stream()
                .map(e -> {
                    final var electionResult = electionResultConverter.to(e);
                    final var name = candidateBOSMap.get(e.getCandidateId()).getName();
                    electionResult.setName(name);
                    return electionResult;
                })
                .toList();

        return GetElectionResultDTO.Rsp.builder()
                .electionResults(electionResults)
                .election(electionResultConverter.to(electionBO))
                .build();
    }

    private ElectionBO check(GetElectionResultDTO.@NonNull Req req, String electionId) {
        final var cacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        final var exists = cacheMap.isExists();
        Assert.isTrue(!exists, () -> {
            log.warn("???????????????????????????????????? param:{}", req);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //??????????????????
        return Optional.ofNullable(electionManager.getByElectionId(electionId)).orElseThrow(() -> {
            log.warn("???????????????????????? param:{}", req);
            return new RuntimeException("????????????????????????");
        });
    }

}
