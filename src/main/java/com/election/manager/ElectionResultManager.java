package com.election.manager;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.election.bo.ElectionResultBO;
import com.election.entity.ElectionResult;
import com.election.mapper.ElectionResultMapper;
import com.election.mapstruct.ElectionResultConverter;
import com.election.query.ElectionResultQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElectionResultManager {

    private final Snowflake snowflake;
    private final ElectionResultMapper electionResultMapper;
    private final ElectionResultConverter electionResultConverter;


    public void batchInsert(@NonNull final List<ElectionResultBO> resultBOS) {
        final var now = LocalDateTime.now();
        final var batchInsertQueries = resultBOS
                .stream()
                .map(e -> electionResultConverter.to(e, now, now))
                .collect(Collectors.toList());

        electionResultMapper.batchInsert(batchInsertQueries);
    }

    public void add(@NonNull final ElectionResultBO electionResultBO, @NotBlank final String candidateId) {
        final var now = LocalDateTime.now();

        final var electionResult = electionResultConverter.toEntity(electionResultBO);
        electionResult.setElectionResultId(snowflake.nextIdStr());
        electionResult.setCreateTime(now);
        electionResult.setUpdateTime(now);
        electionResult.setCandidateId(candidateId);
        electionResultMapper.insert(electionResult);
    }

    public void updateByCandidateId(@NonNull final ElectionResultQuery.UpdateQuery updateQuery) {
        final var electionResult = electionResultConverter.to(updateQuery);

        final var candidateId = updateQuery.getCandidateId();
        electionResultMapper.update(electionResult, Wrappers.<ElectionResult>lambdaUpdate()
                .eq(ElectionResult::getCandidateId, candidateId)
        );
    }

    public List<ElectionResultBO> getByElectionId(@NotBlank final String electionId) {
        final var electionResults = electionResultMapper.selectList(Wrappers.<ElectionResult>lambdaQuery()
                .eq(ElectionResult::getElectionId, electionId));
        return electionResults.stream()
                .map(electionResultConverter::to)
                .collect(Collectors.toList());
    }
}
