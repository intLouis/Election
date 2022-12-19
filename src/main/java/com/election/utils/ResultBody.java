package com.election.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultBody<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public ResultBody(StateCode code, String... filedsName) {
        this.code = code.getCode();
        this.msg = String.format(code.getMsg(), filedsName);
    }

    public ResultBody(StateCode code, T data, String... filedsName) {
        this.code = code.getCode();
        this.msg = String.format(code.getMsg(), filedsName);
        this.data = data;
    }

    public ResultBody(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultBody(T data) {
        this.code = StateCode.SUCCESS.code;
        this.msg = StateCode.SUCCESS.msg;
        this.data = data;
    }

    public static ResultBody success() {
        return ResultBody.builder()
                .code(StateCode.SUCCESS.code)
                .msg(StateCode.SUCCESS.msg)
                .build();
    }

    public static <T> ResultBody<T> success(T data) {

        return new ResultBody<T>(data);
    }

}
