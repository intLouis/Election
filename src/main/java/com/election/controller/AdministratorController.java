package com.election.controller;

import com.election.dto.administrator.*;
import com.election.service.AdministratorService;
import com.election.utils.ResultBody;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;


    @PostMapping("addCandidate")
    public ResultBody addCandidate(@RequestBody @Validated AddCandidateDTO.Req req) {
        administratorService.addCandidate(req);
        return ResultBody.success();
    }

    @PostMapping("addElection")
    public ResultBody addElection(@RequestBody @Validated AddElectionDTO.Req req) {
        administratorService.addElection(req);
        return ResultBody.success();
    }

    @PutMapping("changeState")
    public ResultBody changeState(@RequestBody @Validated StartOrEndElectionDTO.Req req) {
        administratorService.changeState(req);
        return ResultBody.success();
    }

    @GetMapping("voterInfo")
    public ResultBody<GetVoterInfoDTO.Rsp> getVoterInfo(@RequestBody @Validated GetVoterInfoDTO.Req req) {
        return ResultBody.success(administratorService.getVoterInfo(req));
    }


    @GetMapping("electionResult")
    public ResultBody<GetElectionResultDTO.Rsp> electionResult(@RequestBody @Validated GetElectionResultDTO.Req req) {
        return ResultBody.success(administratorService.electionResult(req));
    }

}
