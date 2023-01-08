package com.election.service.bo;

import com.election.api.enums.ElectionState;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ElectionBO implements Serializable {

    /**
     * 物理主键
     */
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

}
