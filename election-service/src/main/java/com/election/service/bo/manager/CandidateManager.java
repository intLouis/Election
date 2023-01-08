package com.election.service.bo.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.election.service.mapper.data.CandidateInProgressDO;
import com.election.service.bo.CandidateBO;
import com.election.service.mapper.entity.Candidate;
import com.election.enums.CacheKey;
import com.election.service.mapper.CandidateMapper;
import com.election.service.mapstruct.CandidateConverter;
import com.election.service.query.CandidateQuery;
import com.election.api.model.PageDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CandidateManager {

    private final RedissonClient redisson;
    private final CandidateMapper candidateMapper;
    private final CandidateConverter candidateConverter;
    private final Snowflake snowflake;

    public List<CandidateBO> getElectionsInProgressInfoByPage(@NonNull final PageDTO.Request req) {

        //从缓存里查
        final var cache = getFromCache();
        if (CollectionUtil.isNotEmpty(cache)) return cache;


        //否则
        //查出进行中的候选人
        final var candidateInProgress = candidateMapper.getCandidateInProgressByPage(req.getAnchor(),
                req.getPageSize());
        //空直接返回
        if (CollectionUtil.isEmpty(candidateInProgress)) return List.of();

        //异步回写到缓存
        forkWriteToCache(candidateInProgress);

        return candidateInProgress.stream()
                .map(candidateConverter::to)
                .collect(Collectors.toList());

    }

    public String add(@NonNull final CandidateQuery.AddCandidate addCandidate) {
        final var candidate = candidateConverter.to(addCandidate);
        final var now = LocalDateTime.now();
        candidate.setCreateTime(now);
        candidate.setUpdateTime(now);
        final var candidateId = snowflake.nextIdStr();
        candidate.setCandidateId(candidateId);
        //插入到候选人表
        candidateMapper.insert(candidate);
        return candidateId;
    }


    public @NonNull List<CandidateBO> getByElectionId(final @NonNull String electionId) {
        final var candidates = candidateMapper
                .selectList(Wrappers.<Candidate>lambdaQuery()
                        .eq(Candidate::getElectionId, electionId));

        return candidates
                .stream()
                .map(candidateConverter::to)
                .collect(Collectors.toList());
    }


    private void forkWriteToCache(@NonNull final List<CandidateInProgressDO> candidateInProgress) {
        CompletableFuture.runAsync(() -> {
            final var map = redisson.getMap(CacheKey.INPROGRESSCANDIDATE.code);
            candidateInProgress.forEach(e -> map.put(e.getCandidateId(), JSONUtil.toJsonStr(e)));
        });
    }

    private @NonNull List<CandidateBO> getFromCache() {
        final var map = redisson.getMap(CacheKey.INPROGRESSCANDIDATE.code);
        if (CollectionUtil.isEmpty(map)) {
            return List.of();
        }

        return map.values().stream()
                .map(o -> JSONUtil.toBean(o.toString(), CandidateBO.class))
                .collect(Collectors.toList());
    }

    public CandidateBO getByIdNumber(final @NotBlank String idNumber) {
        final var candidate = candidateMapper.selectOne(Wrappers.<Candidate>lambdaQuery()
                .eq(Candidate::getIdNumber, idNumber));
        return candidateConverter.to(candidate);
    }

}
