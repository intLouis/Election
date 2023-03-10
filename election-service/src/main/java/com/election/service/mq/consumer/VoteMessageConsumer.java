package com.election.service.mq.consumer;

import com.election.enums.CacheKey;
import com.election.service.bo.manager.ElectionRecordManager;
import com.election.service.mapstruct.ElectionRecordConverter;
import com.example.service.mq.VoteMessage;
import com.election.service.query.ElectionRecordQuery;
import com.election.service.utils.MsgEnum;
import com.election.service.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMap;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
@Slf4j
public class VoteMessageConsumer {
    private final Redisson redisson;
    private final ElectionRecordConverter electionRecordConverter;
    private final ElectionRecordManager electionRecordManager;
    private final RedisUtil redisUtil;
    private final static String key = CacheKey.VOTETASKMESSAGE.code;

    private final static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            5,
            5,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5)
    );


    public void run() throws Exception {

        while (true) {
            final RBlockingQueue<VoteMessage> blockingQueue = redisson.getBlockingQueue(key, JsonJacksonCodec.INSTANCE);
            final VoteMessage voteMessage = blockingQueue.take();
            poolExecutor.submit(() -> {
                this.execute(voteMessage);
            });

        }

    }

    @SneakyThrows
    private void execute(VoteMessage voteMessage) {
        final var electionId = voteMessage.getElectionId();
        final RBloomFilter<String> bloomFilter = redisson.getBloomFilter(CacheKey.getBizCacheKey(CacheKey.VOTEBLOOMFILTER, electionId));
        //????????????
        final String bloomKey = this.checkRepeat(voteMessage, bloomFilter);
        //?????????????????????
        redisUtil.lock(voteMessage.getVoterIdNumber(), () -> {
            try {
                //???????????????DB
                ElectionRecordQuery.InsertQuery insertQuery = electionRecordConverter.to(voteMessage);
                electionRecordManager.insert(insertQuery);

                //??????????????????????????????+1
                final RMap<String, Long> inProgressElectionCacheMap = redisson
                        .getMap(CacheKey.getCandidateVotesCacheKey(electionId), StringCodec.INSTANCE);
                inProgressElectionCacheMap.addAndGet(voteMessage.getCandidateId(), 1);

                //?????????????????????
                bloomFilter.add(bloomKey);
                log.info("VoteMessageConsumer??????????????? param:{}", voteMessage);
            } catch (Exception e) {
                log.error("???????????????????????? e:{}", e.getMessage());
                e.printStackTrace();
            }
        });

    }


    private String checkRepeat(VoteMessage voteMessage, RBloomFilter<String> bloomFilter) {
        final var bloomKey = voteMessage.getVoterIdNumber();


        Assert.isTrue(!bloomFilter.contains(bloomKey), () -> {
            log.error("VoteMessageConsumer ???????????????param:{}", voteMessage);
            return MsgEnum.BAD_REQUEST.desc;
        });
        return bloomKey;
    }
}
