package com.election.api.dto.voter;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public interface VoteDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class Req {

        /**
         * 候选人id
         */
        @NotBlank
        private String candidateId;

        /**
         * 选举场次id
         */
        @NotBlank
        private String electionId;

        /**
         * 选民名称
         */
        @NotBlank
        @Length(max = 20, min = 2)
        private String voterName;

        /**
         * 选民身份证
         */
        @NotBlank
        @Length(max = 10, min = 10)
        private String voterIdNumber;

        /**
         * 选民邮箱
         */
        @NotBlank
        private String voterEmail;
    }

}
