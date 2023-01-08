package com.election.service.service;

import com.election.api.dto.administrator.*;
import lombok.NonNull;

public interface AdministratorService {

    /**
     * 添加候选人
     *
     * @param req
     */
    void addCandidate(@NonNull final AddCandidateDTO.Req req);

    /**
     * 管理员获取指定候选人的选民记录
     *
     * @param req
     * @return
     */
    GetVoterInfoDTO.Rsp getVoterInfo(@NonNull GetVoterInfoDTO.Req req);

    /**
     * 添加选举场次
     *
     * @param req
     */
    void addElection(@NonNull final AddElectionDTO.Req req);


    /**
     * 开始或结束选举
     * @param req
     */
    void changeState(StartOrEndElectionDTO.@NonNull Req req);

    /**
     * 获取选举结果
     * @param req
     * @return
     */
    GetElectionResultDTO.Rsp electionResult(GetElectionResultDTO.Req req);
}
