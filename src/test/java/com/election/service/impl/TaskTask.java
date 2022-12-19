package com.election.service.impl;

import com.election.ElectionApplication;
import com.election.mq.consumer.VoteMessageConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElectionApplication.class})
public class TaskTask {

    @Resource
    private VoteMessageConsumer voteMessageConsumer;

    @Test
    public void VoteMessageConsumerTest() throws Exception {
        voteMessageConsumer.run();
    }
}
