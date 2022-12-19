package com.election.mapstruct;

import com.election.bo.ElectionRecordBO;
import com.election.dto.administrator.GetVoterInfoDTO;
import com.election.dto.voter.VoteDTO;
import com.election.entity.ElectionRecord;
import com.election.mq.VoteMessage;
import com.election.query.ElectionRecordQuery;
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
