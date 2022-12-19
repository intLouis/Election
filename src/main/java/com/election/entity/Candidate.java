package com.election.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Candidate {

    /**
     * 物理主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 候选人id
     */
    private String candidateId;

    /**
     * 选举场次id
     */
    private String electionId;

    /**
     * 候选人姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
