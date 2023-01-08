package com.election.service.bo;

import com.election.enums.EmailSendState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ElectionRecordBO {

    /**
     * 物理主键
     */
    private Long id;
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
