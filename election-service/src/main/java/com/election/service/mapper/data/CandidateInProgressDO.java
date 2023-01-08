package com.election.service.mapper.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CandidateInProgressDO {

    /**
     * 候选人id
     */
    private String candidateId;

    /**
     * 候选人姓名
     */
    private String name;

    /**
     * 选举场次id
     */
    private String electionId;

    /**
     * 得票数
     */
    private Long votes;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
