package com.election.service.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UserContext {

    private String userId;
    private String userName;

    public Optional<String> getUserIdOptional() {
        return Optional.of(this.userId);
    }
}
