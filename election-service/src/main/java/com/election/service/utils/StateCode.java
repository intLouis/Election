package com.election.service.utils;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum StateCode {

    SUCCESS(200, "success"),
    FAIL(100, "fail"),
    ;

    @NonNull
    public final Integer code;
    @NonNull
    public final String msg;

    StateCode(@NonNull Integer code, @NonNull String msg) {
        this.code = code;
        this.msg = msg;
    }
}
