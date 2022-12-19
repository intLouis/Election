package com.election.service;

import com.election.dto.voter.ElectionLiveDTO;
import com.election.dto.voter.VoteDTO;
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
