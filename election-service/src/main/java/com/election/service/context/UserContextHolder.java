package com.election.service.context;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    @Bean("userContext")
    public UserContext getContext() {
        return userContext.get() == null ? new UserContext() : null;
    }

    public static void setContext(UserContext context) {
        userContext.set(context);
    }

    public static void clear() {
        userContext.remove();
    }
}
