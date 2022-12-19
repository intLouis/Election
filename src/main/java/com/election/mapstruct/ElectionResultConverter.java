package com.election.mapstruct;

import com.election.bo.CandidateBO;
import com.election.bo.ElectionBO;
import com.election.bo.ElectionResultBO;
import com.election.dto.administrator.GetElectionResultDTO;
import com.election.entity.ElectionResult;
import com.election.query.ElectionResultQuery;
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
