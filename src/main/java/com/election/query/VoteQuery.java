package com.election.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

public interface VoteQuery {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class DoVote {
        /**
         * 选民Id
         */
        @NonNull
        private String voterId;
        /**
         * 候选人id
         */
        @NonNull
        private String candidateId;

        /**
         * 选举场次id
         */
        @NonNull
        private String electionId;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
    }
}
