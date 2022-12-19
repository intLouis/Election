package com.election.dto.voter;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public interface ElectionLiveDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req {

        @NotBlank
        private String electionId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Rsp {

        @NonNull
        private List<Candidate> candidates;

        @NonNull
        private Election election;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Candidate {

        @NotBlank
        private String candidateId;

        @NotBlank
        private String name;

        @NonNull
        private Integer age;

        @NonNull
        private Long votes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Election {

        @NotBlank
        private String electionId;

        @NotBlank
        private String name;

        @NonNull
        private LocalDateTime startTime;
    }
}
