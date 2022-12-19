package com.election.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.election.bo.CandidateBO;
import com.election.bo.ElectionBO;
import com.election.bo.ElectionRecordBO;
import com.election.bo.ElectionResultBO;
import com.election.dto.administrator.*;
import com.election.enums.CacheKey;
import com.election.enums.ElectionState;
import com.election.manager.CandidateManager;
import com.election.manager.ElectionManager;
import com.election.manager.ElectionRecordManager;
import com.election.manager.ElectionResultManager;
import com.election.mapstruct.CandidateConverter;
import com.election.mapstruct.ElectionConverter;
import com.election.mapstruct.ElectionRecordConverter;
import com.election.mapstruct.ElectionResultConverter;
import com.election.mq.consumer.VoteMessageConsumer;
import com.election.query.ElectionRecordQuery;
import com.election.schedule.FlushVoteInfoToDBTask;
import com.election.service.AdministratorService;
import com.election.utils.EmailTextEnum;
import com.election.utils.MsgEnum;
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

import static com.election.enums.CacheKey.getBizCacheKey;
import static com.election.utils.IdNumberCheck.checkHKIdNumber;

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


    @Override
    public void addCandidate(final AddCandidateDTO.@NonNull Req req) {
        final var addCandidate = candidateConverter.to(req);

        final var electionResultBO = candidateConverter.toElectionResult(addCandidate);

        //获取选举场次分布式锁，防止此时开始造成状态紊乱
        final var addLock = redisson.getLock(getBizCacheKey(CacheKey.ELECTION, req.getElectionId()));
        addLock.lock();
        try {
            //业务逻辑校验
            check(req);
            transactionTemplate.executeWithoutResult(e -> {
                //插入到候选人表
                final var candidateId = candidateManager.add(addCandidate);
                //添加到选举结果表
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
            log.error("选举未处于准备状态或不存在，无法添加候选人 param：{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        //身份证合法性
        final var idNumber = req.getIdNumber();
        final var validHKCard = checkHKIdNumber(idNumber);
        Assert.isTrue(validHKCard, () -> {
            log.error("身份证号码不合法 param：{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        //候选人是否存在
        final var isNoNull = Objects.isNull(candidateManager.getByIdNumber(idNumber));
        Assert.isTrue(isNoNull, () -> {
            log.error("候选人已存在 param：{}", req);
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
        //检查参数
        final var state = req.getState();
        final var electionId = req.getElectionId();
        Assert.isTrue(ElectionState.startOrEnd(state), () -> {
            log.error("请求参数错误，状态参数必须是开始或者结束 electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //分布式锁，锁当前场次，锁隔离
        final var startLock = redisson.getLock(getBizCacheKey(CacheKey.ELECTION, electionId));
        try {
            final var b = startLock.tryLock(5, TimeUnit.SECONDS);
            //加锁失败断言
            Assert.isTrue(b, () -> {
                log.error("分布式锁加锁失败 req:{}", req);
                return MsgEnum.BAD_REQUEST.desc;
            });

            final var electionBO = electionManager.getByElectionId(electionId);
            switch (state) {
                case START -> startElection(electionBO);
                case END -> stopElection(electionBO);
            }

            //状态更改
            electionManager.changeState(electionBO);

        } finally {
            startLock.unlock();
        }
    }


    private void startElection(ElectionBO electionBO) {
        final var electionId = electionBO.getElectionId();

        //断言检查，必须是未开始状态
        final List<CandidateBO> candidateBOS = checkStartElection(electionBO, electionId);

        electionBO.setState(ElectionState.START);
        //设置缓存
        toSetCache(electionBO, candidateBOS);

        //开启异步刷盘任务
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
            log.error("选举已结束或已开始！");
            return MsgEnum.BAD_REQUEST.desc;
        });

        final var candidateBOS = candidateManager.getByElectionId(electionId);

        Assert.isTrue(candidateBOS.size() >= 2, () -> {
            log.error("该场次候选人不足2人，无法开始选举！electionId:{}", electionId);
            return MsgEnum.BUSINESS_LOGIC_ERROR.desc;
        });
        return candidateBOS;
    }


    private void toSetCache(final ElectionBO electionBO, final @NotEmpty List<CandidateBO> candidateBOS) {
        final var electionId = electionBO.getElectionId();

        //该场次候选人信息
        final RMap<String, CandidateBO> inProgressCacheMap = redisson.getMap(CacheKey.getInProgressCandidateCacheKey(electionId));
        //候选人得票信息
        final RMap<String, Long> candidateVotesCacheMap = redisson.getMap(CacheKey.getCandidateVotesCacheKey(electionId), StringCodec.INSTANCE);
        //进行中场次
        final RMap<String, ElectionBO> inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //初始化布隆过滤器
        final RBloomFilter<String> bloomFilter = redisson.getBloomFilter(CacheKey.getBizCacheKey(CacheKey.VOTEBLOOMFILTER, electionId));
        bloomFilter.tryInit(1000, 0.01);

        //查询最新的场次信息
        final var newestElectionBO = electionManager.getByElectionId(electionId);
        inProgressElectionCacheMap.put(electionId, newestElectionBO);

        //写入缓存
        candidateBOS.forEach(item -> {
            inProgressCacheMap.put(item.getCandidateId(), item);
            candidateVotesCacheMap.put(item.getCandidateId(), 0L);
        });


    }

    private void stopElection(final @NonNull ElectionBO electionBO) {
        final var electionId = electionBO.getElectionId();
        //断言检查，必须是开始状态
        Assert.isTrue(Objects.equals(electionBO.getState(), ElectionState.START), () -> {
            log.error("场次状态必须为开始 electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //删除缓存
        deleteCache(electionId);

        //执行一次缓存同步任务，将结果刷入db
        CompletableFuture.runAsync(() -> flushVoteInfoToDBTask.executeTask(electionId));

        //异步发送邮件
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
            //更新查询游标
            anchor = Objects.toString(electionRecordByPage.get(electionRecordByPage.size() - 1).getId());
        }
    }

    private void doSend(@NonNull final ElectionRecordBO electionRecordBO) {
        final var electionId = electionRecordBO.getElectionId();
        final var electionBO = electionManager.getByElectionId(electionId);

        //该场次投票结果
        final var electionResultBOS = electionResultManager.getByElectionId(electionId)
                .stream()
                .collect(Collectors.toMap(ElectionResultBO::getCandidateId, Function.identity()));

        final var candidateBOS = candidateManager.getByElectionId(electionId);
        //构建正文
        final var content = buildResultContent(electionResultBOS, candidateBOS);
        //发邮件
        final String title = EmailTextEnum.getTitleText(EmailTextEnum.SEND_RESULT_CONTENT_TEXT, electionBO.getName());

        try {
            //发送邮件
            MailUtil.send(mailAccount, electionRecordBO.getVoterEmail(), title, content, false);
            //更新发送邮件状态
            electionRecordManager.updateById(electionRecordConverter.toUpdateQuery(electionRecordBO));

            log.info("发送邮件成功 param:{}", electionRecordBO);
        } catch (Exception e) {
            log.warn("邮件发送失败 voterEmail:{}", electionRecordBO.getVoterEmail());
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

        //判断投票结果刷盘任务是否正在进行
        final var lock = redisson.getLock(CacheKey.FLUSHVOTESTODBINPROGRESS.code);
        final boolean tryLock;
        tryLock = lock.tryLock(5, TimeUnit.SECONDS);

        Assert.isTrue(tryLock, () -> {
            log.warn("刷盘任务正在进行，无法结束，请稍后再试！ electionId:{}", electionId);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //该场次候选人信息
        final var inProgressCacheMap = redisson.getMap(CacheKey.getInProgressCandidateCacheKey(electionId));
        //候选人得票信息
        final var candidateVotesCacheMap = redisson.getMap(CacheKey.getCandidateVotesCacheKey(electionId), new StringCodec());
        //进行中场次
        final var inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //当前场次布隆过滤器
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

        //业务逻辑判断
        final var electionBO = check(req, electionId);

        //数据组装

        //结果组装
        final var electionResultBOS = electionResultManager.getByElectionId(electionId);
        //候选人信息
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
            log.warn("竞选未结束，无法查看结果 param:{}", req);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //选举场次信息
        return Optional.ofNullable(electionManager.getByElectionId(electionId)).orElseThrow(() -> {
            log.warn("查询的场次不存在 param:{}", req);
            return new RuntimeException("查询的场次不存在");
        });
    }

}
