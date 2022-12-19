package com.election;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@MapperScan("com.election.mapper")
public class ElectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectionApplication.class, args);
    }

}
