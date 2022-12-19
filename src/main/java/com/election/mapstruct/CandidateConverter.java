package com.election.mapstruct;

import com.election.bo.CandidateBO;
import com.election.bo.ElectionResultBO;
import com.election.dto.administrator.AddCandidateDTO;
import com.election.dto.voter.ElectionLiveDTO;
import com.election.entity.Candidate;
import com.election.mapper.data.CandidateInProgressDO;
import com.election.query.CandidateQuery;
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
