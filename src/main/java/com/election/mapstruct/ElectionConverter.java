package com.election.mapstruct;

import com.election.bo.ElectionBO;
import com.election.dto.administrator.AddElectionDTO;
import com.election.dto.voter.ElectionLiveDTO;
import com.election.entity.Election;
import com.election.query.ElectionQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ElectionConverter {

    ElectionQuery.UpdateElectionState toUpdateElectionState(ElectionBO electionBO);

    Election to(ElectionQuery.AddElectionQuery addElectionQuery);

    ElectionQuery.AddElectionQuery to(AddElectionDTO.Req req);

    ElectionBO to(Election election);

    ElectionLiveDTO.Election to(ElectionBO electionBO);
}
