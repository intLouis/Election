package com.election.utils;

public enum MsgEnum {

    ERROR(400, "数据错误"),
    NOT_FOUND(401, "数据不存在"),
    BAD_REQUEST(402, "错误请求"),
    BUSINESS_LOGIC_ERROR(403, "业务逻辑错误"),
    ILLEGAL_PARAMETER(405, "非法参数");
    public final Integer code;
    public final String desc;

    MsgEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
