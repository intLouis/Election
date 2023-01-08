package com.election.service.utils.exception;

public enum ExceptionEnum {

    UNKNOWN_DATA(400, "数据不存在"),
    ;
    public final Integer code;
    public final String desc;

    ExceptionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
