package com.liaison.hbase.exception;

public class HBaseInitializationException extends HBaseRuntimeException {

    private static final long serialVersionUID = 4050120586274690725L;
    
    public HBaseInitializationException(final String message) {
        super(message);
    }
    public HBaseInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseInitializationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
