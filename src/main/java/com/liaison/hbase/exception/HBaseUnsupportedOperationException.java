package com.liaison.hbase.exception;

public class HBaseUnsupportedOperationException extends HBaseException {

    private static final long serialVersionUID = -7243882672508779618L;
    
    public HBaseUnsupportedOperationException(String message) {
        super(message);
    }
    public HBaseUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseUnsupportedOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
