package com.election.service.mapstruct;

import com.election.api.dto.administrator.AddCandidateDTO;
import com.election.api.dto.voter.ElectionLiveDTO;
import com.election.service.bo.CandidateBO;
import com.election.service.bo.ElectionResultBO;
import com.election.service.mapper.data.CandidateInProgressDO;
import com.election.service.mapper.entity.Candidate;
import com.election.service.query.CandidateQuery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateConverter {

    CandidateBO to(CandidateInProgressDO inProgressDO);

    Candidate to(CandidateQuery.AddCandidate candidate);

    CandidateQuery.AddCandidate to(AddCandidateDTO.Req req);

    CandidateBO to(Candidate candidate);

    ElectionResultBO toElectionResult(CandidateQuery.AddCandidate addCandidate);


    ElectionLiveDTO.Candidate to(CandidateBO candidateBO, Long votes);
}
