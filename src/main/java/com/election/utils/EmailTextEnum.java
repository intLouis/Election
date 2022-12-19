package com.election.utils;

import javax.validation.constraints.NotBlank;

public enum EmailTextEnum {
    SEND_RESULT_CONTENT_TEXT("您投票的选举：%s，已结束，下面是各个候选人得票情况，请查收！"),
    ;
    public final String code;

    EmailTextEnum(String code) {
        this.code = code;
    }

    public static String getTitleText(EmailTextEnum textEnum, @NotBlank String electionIdName) {
        return String.format(textEnum.code, electionIdName);
    }
}
