package com.election.query;

import com.election.enums.ElectionState;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;

public interface ElectionQuery {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class AddElectionQuery {

        @NonNull
        private String name;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class UpdateElectionState {

        @NonNull
        @Positive
        private Long id;

        @NonNull
        private String electionId;

        @NonNull
        private ElectionState state;
    }
}
