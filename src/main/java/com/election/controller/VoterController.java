package com.election.controller;

import com.election.dto.voter.ElectionLiveDTO;
import com.election.dto.voter.VoteDTO;
import com.election.service.VoterService;
import com.election.utils.ResultBody;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voter")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService voterService;


    @PutMapping("vote")
    public ResultBody vote(@RequestBody @Validated VoteDTO.Req req) {
        voterService.vote(req);
        return ResultBody.success();
    }


    @GetMapping("live")
    public ResultBody<ElectionLiveDTO.Rsp> live(@RequestBody @Validated ElectionLiveDTO.Req req){
        return ResultBody.success(voterService.getElectionLive(req));
    }

}
