package com.election.service.service.impl;

import cn.hutool.core.lang.Validator;
import com.election.api.dto.voter.ElectionLiveDTO;
import com.election.api.dto.voter.VoteDTO;
import com.election.api.enums.CacheKey;
import com.election.api.mto.VoteMessage;
import com.election.service.bo.CandidateBO;
import com.election.service.bo.ElectionBO;
import com.election.service.bo.manager.CandidateManager;
import com.election.service.bo.manager.ElectionManager;
import com.election.service.mapstruct.CandidateConverter;
import com.election.service.mapstruct.ElectionConverter;
import com.election.service.mapstruct.ElectionRecordConverter;
import com.election.service.service.VoterService;
import com.election.service.utils.IdNumberCheck;
import com.election.service.utils.MsgEnum;
import com.election.service.utils.exception.BusinessException;
import com.election.service.utils.exception.ExceptionEnum;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.election.api.enums.CacheKey.getBizCacheKey;


@Service
@RequiredArgsConstructor
@Slf4j
public class VoterServiceImpl implements VoterService {

    private final ElectionManager electionManager;
    private final CandidateManager candidateManager;
    private final ElectionConverter electionConverter;
    private final CandidateConverter candidateConverter;
    private final ElectionRecordConverter electionRecordConverter;
    private final Redisson redisson;

    @Override
    public void vote(VoteDTO.@NonNull Req req) {

        final var electionId = req.getElectionId();


        //???????????????????????????
        final RMap<String, ElectionBO> inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //???????????????????????????
        final var candidateCache = redisson.getMap(getBizCacheKey(CacheKey.INPROGRESSCANDIDATE, electionId));
        //??????????????????
        this.voteCheck(req, inProgressElectionCacheMap, candidateCache);

        //??????????????????
        final RBlockingQueue<VoteMessage> blockingQueue = redisson.getBlockingQueue(CacheKey.VOTETASKMESSAGE.code, JsonJacksonCodec.INSTANCE);
        blockingQueue.offer(electionRecordConverter.to(req));

    }

    private void voteCheck(VoteDTO.@NonNull Req req, RMap<String, ElectionBO> stringElectionBORMap, RMap<Object, Object> candidateCache) {

        final var candidateId = req.getCandidateId();
        final var electionId = req.getElectionId();

        //??????????????????????????????????????????
        Assert.isTrue(stringElectionBORMap.containsKey(electionId), "??????????????????????????????");

        //????????????
        final RBloomFilter<String> bloomFilter = redisson.getBloomFilter(getBizCacheKey(CacheKey.VOTEBLOOMFILTER, electionId));

        final var contains = bloomFilter.contains(req.getVoterIdNumber());
        Assert.isTrue(!contains, () -> {
            log.warn("?????????????????? param???{}", req);
            return MsgEnum.BAD_REQUEST.desc;
        });

        //??????????????????
        final var validHKCard = IdNumberCheck.checkHKIdNumber(req.getVoterIdNumber());
        Assert.isTrue(validHKCard, () -> {
            log.warn("???????????????????????? param???{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        final var isEmail = Validator.isEmail(req.getVoterEmail());

        Assert.isTrue(isEmail, () -> {
            log.warn("??????????????? param???{}", req);
            return MsgEnum.ILLEGAL_PARAMETER.desc;
        });
        //???????????????????????????
        Optional.ofNullable(candidateCache.get(candidateId)).orElseThrow(() -> {
            log.warn("?????????????????????????????? param:{}", req);
            return new BusinessException(ExceptionEnum.UNKNOWN_DATA);
        });
    }

    @Override
    public ElectionLiveDTO.Rsp getElectionLive(ElectionLiveDTO.@NonNull Req req) {

        final var electionId = req.getElectionId();

        //???????????????????????????????????????
        final var electionBO = check(req, electionId);

        //?????????????????????????????????
        final var candidateCache = getFromCache(electionId);

        final var election = electionConverter.to(electionBO);


        return ElectionLiveDTO.Rsp.builder()
                .election(election)
                .candidates(candidateCache)
                .build();
    }

    private List<ElectionLiveDTO.Candidate> getFromCache(@NonNull final String electionId) {
        final var candidateVotesCacheKey = CacheKey.getCandidateVotesCacheKey(electionId);
        final var inProgressCandidateCacheKey = CacheKey.getInProgressCandidateCacheKey(electionId);


        final RMap<String, String> candidateVotesMapCache = redisson.getMap(candidateVotesCacheKey, StringCodec.INSTANCE);
        final RMap<String, CandidateBO> inProgressCandidateMapCache = redisson.getMap(inProgressCandidateCacheKey);

        //??????????????????????????????????????????
        Assert.isTrue(candidateVotesMapCache.isExists() && inProgressCandidateMapCache.isExists(), () -> {
            log.error("??????????????????????????????????????????????????????????????? electionId:{}", electionId);
            return MsgEnum.NOT_FOUND.desc;
        });

        //????????????
        return candidateVotesMapCache.entrySet()
                .stream()
                .map(e -> buildCandidate(inProgressCandidateMapCache, e))
                .collect(Collectors.toList());

    }

    private ElectionLiveDTO.@NonNull Candidate buildCandidate(RMap<String, CandidateBO> inProgressCandidateMapCache, Map.Entry<String, String> candidateVotesEntry) {
        final var key = candidateVotesEntry.getKey();
        final var votes = candidateVotesEntry.getValue();
        final var candidateBO = inProgressCandidateMapCache.get(key);

        return candidateConverter.to(candidateBO,Long.valueOf(votes));
    }


    private ElectionBO check(ElectionLiveDTO.@NonNull Req req, @NotEmpty final String electionId) {
        //???????????????
        final RMap<String, ElectionBO> inProgressElectionCacheMap = redisson.getMap(CacheKey.INPROGRESELECTION.code);
        //?????????????????????????????????????????????????????????????????????


        Assert.isTrue(inProgressElectionCacheMap.containsKey(electionId), () -> {
            log.error("??????????????????????????????param:{}", req);
            return MsgEnum.BAD_REQUEST.desc;
        });

        return electionManager.getByElectionId(electionId);
    }
}
