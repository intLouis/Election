package com.election.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@Validated
public class RedisUtil {

    private final Redisson redisson;
    private final Integer releaseTime = 5;
    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    @SneakyThrows
    public void lock(@NotBlank final String key, Integer releaseTime, TimeUnit timeUnit, Runnable task) {
        RLock lock = redisson.getLock(key);
        if (Objects.isNull(timeUnit)) timeUnit = this.timeUnit;
        if (Objects.isNull(releaseTime)) releaseTime = this.releaseTime;
        try {
            if (lock.tryLock(releaseTime, timeUnit)) {
                task.run();
                lock.unlock();
            }
        } catch (InterruptedException e) {
            log.error("分布式锁加锁失败！ e:{}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (!Objects.isNull(lock) && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @SneakyThrows
    public void lock(final @NotBlank String key,Runnable callback) {
        RLock lock = redisson.getLock(key);
        try {
            if (lock.tryLock(this.releaseTime, this.timeUnit)) {
                callback.run();
            }
        } catch (InterruptedException e) {
            log.error("分布式锁加锁失败！ e:{}", e.getMessage());
            e.printStackTrace();
        } finally {
            if (!Objects.isNull(lock) && lock.isLocked()) {
                lock.unlock();
            }
        }
    }


}
