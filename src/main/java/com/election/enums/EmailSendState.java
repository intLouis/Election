package com.election.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.NonNull;

public enum EmailSendState {
    SENT(1, "SENT"),
    NOTSENT(0, "NOTSENT"),
    ;

    @NonNull
    @EnumValue
    public final Integer code;
    @NonNull
    public final String desc;


    EmailSendState(@NonNull Integer code, @NonNull String desc) {
        this.code = code;
        this.desc = desc;
    }
}
