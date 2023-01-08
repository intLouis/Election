package com.election.api.dto.administrator;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public interface GetElectionResultDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req {

        @NotBlank
        private String electionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Rsp {

        @NonNull
        private Election election;

        @NonNull
        @NotEmpty
        private List<ElectionResult> electionResults;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Election {
        /**
         * 选举场次id
         */
        @NotBlank
        private String electionId;

        /**
         * 场次名称
         */
        @NotBlank
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class ElectionResult {

        /**
         * 候选人id
         */
        @NotBlank
        private String candidateId;

        /**
         * 候选人姓名
         */
        @NotBlank
        private String name;

        /**
         * 得票数
         */
        @Positive
        @NonNull
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
}
