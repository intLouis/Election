package com.election.service.mapper.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElectionResult {

    /**
     * 物理主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 选举结果id
     */
    private String electionResultId;
    /**
     * 选举场次id
     */
    private String electionId;

    /**
     * 候选人id
     */
    private String candidateId;

    /**
     * 得票数
     */
    private Long votes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
