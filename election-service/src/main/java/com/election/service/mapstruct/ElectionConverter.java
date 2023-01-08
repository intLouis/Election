package com.election.service.mapstruct;

import com.election.api.dto.administrator.AddElectionDTO;
import com.election.api.dto.voter.ElectionLiveDTO;
import com.election.service.bo.ElectionBO;
import com.election.service.mapper.entity.Election;
import com.election.service.query.ElectionQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ElectionConverter {

    ElectionQuery.UpdateElectionState toUpdateElectionState(ElectionBO electionBO);

    Election to(ElectionQuery.AddElectionQuery addElectionQuery);

    ElectionQuery.AddElectionQuery to(AddElectionDTO.Req req);

    ElectionBO to(Election election);

    ElectionLiveDTO.Election to(ElectionBO electionBO);
}
