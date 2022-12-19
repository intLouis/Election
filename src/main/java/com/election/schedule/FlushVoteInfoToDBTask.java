package com.election.schedule;

import cn.hutool.core.collection.CollectionUtil;
import com.election.bo.ElectionBO;
import com.election.enums.CacheKey;
import com.election.manager.ElectionManager;
import com.election.manager.ElectionResultManager;
import com.election.query.ElectionResultQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class FlushVoteInfoToDBTask {

    private final Redisson redisson;
    private final ElectionResultManager electionResultManager;
    private final ElectionManager electionManager;
    private final static String inProgressElectionKey = CacheKey.INPROGRESELECTION.code;
    private final static String lockKey = CacheKey.FLUSHVOTESTODBINPROGRESS.code;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void run() throws InterruptedException {

        //获取锁，保证任务未结束
        final var lock = redisson.getLock(lockKey);
        final var isLockSuccess = lock.tryLock(5, TimeUnit.SECONDS);
        //如果获取不到锁
        if (!isLockSuccess) {
            log.info("有选举正在结束，无法执行刷盘任务！");
            return;
        }

        //获取进行中的选举场次
        final RMap<String, ElectionBO> inProgressElectionCacheMap = redisson.getMap(inProgressElectionKey);
        if (CollectionUtil.isEmpty(inProgressElectionCacheMap)) {
            lock.unlock();
            log.info("FlushVoteInfoToDBTask: 当前没有正在进行中的选举");
            return;
        }

        //执行刷盘任务
        inProgressElectionCacheMap.keySet().forEach(this::executeTask);

        lock.unlock();
        log.info("投票结果已刷入MySQL time:{} task:{}", LocalDateTime.now(), inProgressElectionCacheMap);


    }


    public void executeTask(final @NotBlank String electionId) {
        //候选人得票信息
        final Map<String, String> candidateVotesCacheMap = redisson.getMap(CacheKey.getCandidateVotesCacheKey(electionId), StringCodec.INSTANCE);
        //将投票结果刷入mysql
        candidateVotesCacheMap.entrySet()
                .stream()
                .map(e -> {
                    final var value = Long.valueOf(e.getValue());
                    final var key = e.getKey();
                    return buildQuery(electionId, key, value);
                })
                .forEach(electionResultManager::updateByCandidateId);
    }


    private ElectionResultQuery.UpdateQuery buildQuery(@NotBlank final String electionId, @NotBlank final String candidateId, Long voteCount) {
        return ElectionResultQuery.UpdateQuery.builder()
                .electionId(electionId)
                .candidateId(candidateId)
                .votes(voteCount)
                .updateTime(LocalDateTime.now())
                .build();
    }


}
