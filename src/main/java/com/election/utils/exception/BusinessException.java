package com.election.utils.exception;

public class BusinessException extends RuntimeException{


    public BusinessException() {

    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.desc);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
