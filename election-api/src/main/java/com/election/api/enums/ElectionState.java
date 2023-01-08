package com.election.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.NonNull;

public enum ElectionState {
    STANDBY(1, "STANDBY"),
    START(2, "START"),
    END(0, "END"),
    ;

    @NonNull
    @EnumValue
    public final Integer code;
    @NonNull
    public final String desc;

    ElectionState(@NonNull Integer code, @NonNull String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Boolean startOrEnd(ElectionState state) {
        if (state.equals(START) || state.equals(END)) return true;
        return false;
    }

}
