package com.election.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateBO implements Serializable {
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



}
