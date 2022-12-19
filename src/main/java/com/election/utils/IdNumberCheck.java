package com.election.utils;

import javax.validation.constraints.NotBlank;

public class IdNumberCheck {

    private final static Character[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


    public static boolean checkHKIdNumber(@NotBlank final String idNumber) {
        final var chars = idNumber.toCharArray();

        //第一位大写字母
        final var upperCase = Character.isUpperCase(chars[0]);

        boolean flag = false;
        //1-6位是数字
        for (int i = 1; i <= 6; i++) {
            flag = Character.isDigit(chars[i]);
        }

        //第9位数字
        final var digit = Character.isDigit(chars[8]);

        //第8第10位
        final var b = chars[7] == '(';
        final var b1 = chars[9] == ')';
        return upperCase && flag && digit && b && b1 && idNumber.length() == 10;
    }

    public static void main(String[] args) {
        System.out.println(checkHKIdNumber("A123456(7)"));
    }
}
