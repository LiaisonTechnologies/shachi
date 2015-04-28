package com.liaison.hbase.exception;

public class HBaseQueryException extends HBaseException {

    private static final long serialVersionUID = -4322216324995327609L;
    
    public HBaseQueryException(String message) {
        super(message);
    }
    public HBaseQueryException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
