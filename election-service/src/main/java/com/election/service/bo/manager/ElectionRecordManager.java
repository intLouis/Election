package com.election.service.bo.manager;

import cn.hutool.core.lang.Snowflake;
import com.election.service.bo.ElectionRecordBO;
import com.election.service.query.ElectionRecordQuery;
import com.election.enums.EmailSendState;
import com.election.service.mapper.ElectionRecordMapper;
import com.election.service.mapstruct.ElectionRecordConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ElectionRecordManager {

    private final ElectionRecordMapper electionRecordMapper;
    private final ElectionRecordConverter electionRecordConverter;
    private final Snowflake snowflake;


    public List<ElectionRecordBO> getElectionRecordByPage(final @NonNull ElectionRecordQuery.GetElectionRecordByPageQuery query) {

        final var electionRecords = electionRecordMapper.getByCandidateIdAndElectionIdPage(query);
        return electionRecords.stream()
                .map(electionRecordConverter::to)
                .collect(Collectors.toList());

    }

    public void insert(@NonNull final ElectionRecordQuery.InsertQuery insertQuery) {
        final var now = LocalDateTime.now();
        final var electionRecord = electionRecordConverter.to(insertQuery);
        electionRecord.setCreateTime(now);
        electionRecord.setUpdateTime(now);
        electionRecord.setEmailSendState(EmailSendState.NOTSENT);
        electionRecord.setElectionRecordId(snowflake.nextIdStr());
        electionRecordMapper.insert(electionRecord);
    }


    public void updateById(@NonNull final ElectionRecordQuery.UpdateStateQuery updateStateQuery) {
        final var electionRecord = electionRecordConverter.to(updateStateQuery);
        electionRecord.setUpdateTime(LocalDateTime.now());
        electionRecord.setEmailSendState(EmailSendState.SENT);
        electionRecordMapper.updateById(electionRecord);
    }

}
