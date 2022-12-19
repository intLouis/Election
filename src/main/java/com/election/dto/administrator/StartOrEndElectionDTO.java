package com.election.dto.administrator;

import com.election.enums.ElectionState;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

public interface StartOrEndElectionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req {

        @NonNull
        @NotBlank
        private String electionId;
        /**
         * 状态
         */
        @NonNull
        private ElectionState state;
    }
}
