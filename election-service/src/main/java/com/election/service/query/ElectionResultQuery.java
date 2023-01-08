package com.election.service.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public interface ElectionResultQuery {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class BatchInsertQuery {

        /**
         * 选举场次id
         */
        @NonNull
        private String electionId;

        /**
         * 候选人id
         */
        @NonNull
        private String candidateId;

        /**
         * 得票数
         */
        private Long votes;

        /**
         * 创建时间
         */
        @NonNull
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        @NonNull
        private LocalDateTime updateTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class InsertQuery {

        /**
         * 选举场次id
         */
        @NonNull
        private String electionId;

        /**
         * 候选人id
         */
        @NonNull
        private String candidateId;

        /**
         * 得票数
         */
        private Long votes;

        /**
         * 创建时间
         */
        @NonNull
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        @NonNull
        private LocalDateTime updateTime;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class UpdateQuery {


        /**
         * 选举场次id
         */
        @NotBlank
        private String electionId;

        /**
         * 候选人id
         */
        @NotBlank
        private String candidateId;

        /**
         * 得票数
         */
        @Positive
        @NonNull
        private Long votes;

        /**
         * 更新时间
         */
        @NonNull
        private LocalDateTime updateTime;
    }
}
