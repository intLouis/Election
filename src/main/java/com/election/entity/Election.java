package com.election.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.election.enums.ElectionState;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Election {

    /**
     * 物理主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 选举场次id
     */
    private String electionId;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 选举场次状态，未开始，已开始，已结束
     */
    private ElectionState state;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
