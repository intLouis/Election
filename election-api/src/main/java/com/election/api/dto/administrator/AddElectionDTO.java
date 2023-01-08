package com.election.api.dto.administrator;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public interface AddElectionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Req {

        @NonNull
        @NotBlank
        @Length(max = 50, min = 5)
        private String name;
    }
}
