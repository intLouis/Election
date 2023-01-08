package com.election.service.mapstruct;

import com.election.api.dto.administrator.GetVoterInfoDTO;
import com.election.api.dto.voter.VoteDTO;
import com.election.api.mto.VoteMessage;
import com.election.service.bo.ElectionRecordBO;
import com.election.service.mapper.entity.ElectionRecord;
import com.election.service.query.ElectionRecordQuery;
import lombok.NonNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ElectionRecordConverter {

    ElectionRecordQuery.GetElectionRecordByPageQuery to(final @NonNull GetVoterInfoDTO.Req req);

    ElectionRecordBO to(final @NonNull ElectionRecord electionRecord);

    GetVoterInfoDTO.ElectionRecord to(ElectionRecordBO electionRecordBO);

    VoteMessage to(VoteDTO.Req req);

    ElectionRecord to(ElectionRecordQuery.InsertQuery insertQuery);

    ElectionRecordQuery.InsertQuery to(VoteMessage voteMessage);

    ElectionRecord to(ElectionRecordQuery.UpdateStateQuery updateStateQuery);

    ElectionRecordQuery.UpdateStateQuery toUpdateQuery(ElectionRecordBO electionRecordBO);

}
