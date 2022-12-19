package com.election.service.impl;

import com.election.ElectionApplication;
import com.election.dto.voter.ElectionLiveDTO;
import com.election.dto.voter.VoteDTO;
import com.election.service.VoterService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.mockito.internal.matchers.text.ValuePrinter.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElectionApplication.class})
class VoterServiceImplTest {
    @Resource
    private VoterService voterService;

    @Test
    void vote() {
        voterService.vote(VoteDTO.Req.builder()
                .candidateId("1600537365729222656")
                .electionId("1600537184858251264")
                .voterEmail("378435665@qq.com")
                .voterName("路人甲")
                .voterIdNumber("Q123456(7)")
                .build());
    }

    @Test
    void getElectionLive() {
        final var electionLive = voterService.getElectionLive(ElectionLiveDTO.Req.builder()
                .electionId("1600537184858251264")
                .build());
        print(electionLive);
    }
}