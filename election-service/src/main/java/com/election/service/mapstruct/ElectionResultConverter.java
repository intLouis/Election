package com.election.service.mapstruct;

import com.election.api.dto.administrator.GetElectionResultDTO;
import com.election.service.bo.CandidateBO;
import com.election.service.bo.ElectionBO;
import com.election.service.bo.ElectionResultBO;
import com.election.service.mapper.entity.ElectionResult;
import com.election.service.query.ElectionResultQuery;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ElectionResultConverter {

    ElectionResultQuery.BatchInsertQuery to(ElectionResultBO electionResultBO,
                                            LocalDateTime createTime,
                                            LocalDateTime updateTime);

    ElectionResultBO to(CandidateBO candidateBO);


    ElectionResult toEntity(ElectionResultBO electionResultBO);

     ElectionResult  to(ElectionResultQuery.UpdateQuery updateQuery);

    ElectionResultBO  to(ElectionResult electionResult);

    GetElectionResultDTO.ElectionResult to(ElectionResultBO electionResultBO);


    GetElectionResultDTO.Election to(ElectionBO electionBO);
}
