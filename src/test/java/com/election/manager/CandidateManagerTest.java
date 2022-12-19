package com.election.manager;

import cn.hutool.core.lang.Snowflake;
import com.election.ElectionApplication;
import com.election.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElectionApplication.class})
class CandidateManagerTest {

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private Snowflake snowflake;

    @Test
    void test1() {
        final var l = snowflake.nextIdStr();
        System.out.println(l);
    }

    @Test
    void test2() {
        redisUtil.lock(null, () -> {

        });
    }
}