package com.election.service.utils;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CustomIdGenerator {

    @Bean
    public Snowflake generateId() {
        return new Snowflake();
    }
}