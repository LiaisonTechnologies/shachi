package com.liaison.hbase.exception;

public class HBaseQueryInputValidationException extends HBaseQueryException {

    private static final long serialVersionUID = 583626977013787059L;
    
    public HBaseQueryInputValidationException(String message) {
        super(message);
    }
    public HBaseQueryInputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseQueryInputValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
