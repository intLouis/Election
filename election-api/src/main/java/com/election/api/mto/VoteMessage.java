package com.election.api.mto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class VoteMessage implements Serializable {

    /**
     * 候选人id
     */
    @NonNull
    private String candidateId;

    /**
     * 选举场次id
     */
    @NonNull
    private String electionId;

    /**
     * 选民名称
     */
    @NotBlank
    @Length(max = 20, min = 2)
    private String voterName;

    /**
     * 选民身份证
     */
    @NotBlank
    @Length(max = 10, min = 10)
    private String voterIdNumber;

    /**
     * 选民邮箱
     */
    @NotBlank
    private String voterEmail;

}
