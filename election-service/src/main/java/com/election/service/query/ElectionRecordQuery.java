package com.election.service.query;

import com.election.enums.EmailSendState;
import com.election.api.model.PageDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public interface ElectionRecordQuery {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class GetElectionRecordByPageQuery extends PageDTO.Request {

        /**
         * 场次id
         */
        @NotBlank
        private String electionId;

        /**
         * 候选人id
         */
        private String candidateId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class InsertQuery {
        /**
         * 选民名称
         */
        @NotBlank
        private String voterName;

        /**
         * 选民身份证
         */
        @NotBlank
        private String voterIdNumber;

        /**
         * 选民邮箱
         */
        @NotBlank
        private String voterEmail;

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

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class UpdateStateQuery {

        /**
         * 物理主键
         */
        @NonNull
        @Positive
        private Long id;

        /**
         * 结果邮件发送状态：未发送，已发送
         */
        @NonNull
        private EmailSendState emailSendState;
    }
}
