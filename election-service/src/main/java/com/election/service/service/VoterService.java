package com.election.service.service;

import com.election.api.dto.voter.ElectionLiveDTO;
import com.election.api.dto.voter.VoteDTO;
import lombok.NonNull;

public interface VoterService {

    /**
     * 投票
     */
    void vote(@NonNull final VoteDTO.Req req);

    /**
     * 查看所投票以后结果
     */
    ElectionLiveDTO.Rsp getElectionLive(@NonNull final ElectionLiveDTO.Req req);



}
