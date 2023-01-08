package com.election.api.dto.administrator;

import com.election.api.enums.EmailSendState;
import com.election.api.model.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface GetVoterInfoDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req extends PageDTO.Request {

        /**
         * 场次id
         */
        @NotBlank
        private String electionId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Rsp extends PageDTO.Response {

       private List<ElectionRecord> electionRecords;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class ElectionRecord{
        /**
         * 选民id
         */
        private String electionRecordId;

        /**
         * 选民名称
         */
        private String voterName;

        /**
         * 选民身份证
         */
        private String voterIdNumber;

        /**
         * 选民邮箱
         */
        private String voterEmail;

        /**
         * 选举场次id
         */
        private String electionId;

        /**
         * 候选人id
         */
        private String candidateId;

        /**
         * 结果邮件发送状态：未发送，已发送
         */
        private EmailSendState emailSendState;
    }
}
