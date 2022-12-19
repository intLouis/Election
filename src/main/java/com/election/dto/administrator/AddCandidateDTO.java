package com.election.dto.administrator;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public interface AddCandidateDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req {

        @NotBlank
        @Length(min = 2, max = 20)
        private String name;

        @NonNull
        @Max(65)
        @Min(18)
        private Integer age;

        @NotBlank
        @Length(min = 10, max = 10)
        private String idNumber;

        @NotBlank
        private String electionId;

    }
}
