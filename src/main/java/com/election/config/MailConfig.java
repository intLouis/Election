package com.election.config;

import cn.hutool.extra.mail.MailAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {

    @Bean
    public MailAccount getMailAccount() {
        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(587);
        account.setAuth(true);
        account.setFrom("xxxxxxxxx@qq.com");
        account.setUser("xxxxxxxxx@qq.com");
        account.setPass("xxxxxxxxxxxxxxxx");
        return account;
    }
}
