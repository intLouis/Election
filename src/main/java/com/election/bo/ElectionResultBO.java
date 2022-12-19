package com.election.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ElectionResultBO {


    /**
     * 选举场次id
     */
    private String electionId;
    /**
     * 选举结果id
     */
    private String electionResultId;

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
