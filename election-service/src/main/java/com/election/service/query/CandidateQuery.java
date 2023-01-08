package com.election.service.query;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

public interface CandidateQuery {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    class GetCandidateInProgress {
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

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class AddCandidate {

        @NotBlank
        private String name;

        @NotBlank
        private Integer age;

        @NotBlank
        private String idNumber;

        @NotBlank
        private String electionId;

    }


}
