package com.election.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.election.enums.EmailSendState;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElectionRecord {

    /**
     * 物理主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 投票记录id
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
