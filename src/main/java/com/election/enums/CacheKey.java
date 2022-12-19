package com.election.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

public enum CacheKey {
    INPROGRESSCANDIDATE("INPROGRESSCANDIDATE", "进行中的候选人"),
    INPROGRESELECTION("INPROGRESELECTION", "进行中场次"),
    CANDIDATEVOTES("CANDIDATEVOTES", "候选人实时得票"),
    FLUSHVOTESTODBINPROGRESS("FLUSHVOTESTODBINPROGRESS","正在执行将得票数据刷入DB任务"),
    VOTETASKMESSAGE("VOTETASK","选民投票消息"),

    VOTEBLOOMFILTER("VOTEBLOOMFILTER","布隆过滤器"),
    ELECTION("ELECTION", "选举场次");


    @NonNull
    @EnumValue
    public final String code;
    @NonNull
    public final String desc;

    CacheKey(@NonNull String code, @NonNull String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getBizCacheKey(CacheKey cacheKey, @NonNull @NotBlank String bizKey) {
        return cacheKey.code.concat(":").concat(bizKey);
    }

    public static String getCandidateVotesCacheKey(final @NonNull @NotBlank String key) {
        return CANDIDATEVOTES.code.concat(":").concat(key);
    }

    public static String getInProgressCandidateCacheKey(final @NonNull @NotBlank String key) {
        return INPROGRESSCANDIDATE.code.concat(":").concat(key);
    }

    public static String getInProgressElectionCacheKey(final @NonNull @NotBlank String key) {
        return INPROGRESELECTION.code.concat(":").concat(key);
    }
}
