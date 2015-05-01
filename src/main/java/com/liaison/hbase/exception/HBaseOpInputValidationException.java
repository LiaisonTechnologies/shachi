package com.liaison.hbase.exception;

public class HBaseOpInputValidationException extends HBaseException {

    private static final long serialVersionUID = 583626977013787059L;
    
    public HBaseOpInputValidationException(String message) {
        super(message);
    }
    public HBaseOpInputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseOpInputValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
