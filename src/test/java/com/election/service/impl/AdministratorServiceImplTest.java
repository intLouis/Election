package com.election.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.election.ElectionApplication;
import com.election.dto.administrator.*;
import com.election.enums.ElectionState;
import com.election.service.AdministratorService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElectionApplication.class})
class AdministratorServiceImplTest {
    @Resource
    private AdministratorService administratorService;
    @Autowired
    private Snowflake snowflake;

    @Test
    void addElection() {
        administratorService.addElection(AddElectionDTO.Req.builder()
                .name("洪兴话事人选举大会")
                .build());
    }

    @Test
    void addCandidate() {
        administratorService.addCandidate(AddCandidateDTO.Req.builder()
                .name("光头强")
                .age(18)
                .idNumber("A123456(7)")
                .electionId("1600537184858251264")
                .build()
        );
    }



    @Test
    void changeState() {
        //开始
//        administratorService.changeState(StartOrEndElectionDTO.Req.builder()
//                .state(ElectionState.START)
//                .electionId("1600537184858251264")
//                .build());
        //结束
        administratorService.changeState(StartOrEndElectionDTO.Req.builder()
                .electionId("1600537184858251264")
                .state(ElectionState.END)
                .build());
    }



    @Test
    void getVoterInfo() {
        final var voterInfo = administratorService.getVoterInfo(GetVoterInfoDTO.Req.builder()
                .electionId("1600537184858251264")
                .pageSize(100)
                .build());
        System.out.println(voterInfo);
    }

    @Test
    void electionResult() {
        final var electionResult = administratorService.electionResult(GetElectionResultDTO.Req.builder()
                .electionId("1600537184858251264")
                .build());

        System.out.println(electionResult);
    }
}