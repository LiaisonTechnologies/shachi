package com.liaison.hbase.exception;

public class HBaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8921980270867671132L;
    
    public HBaseRuntimeException(final String message) {
        super(message);
    }
    public HBaseRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseRuntimeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
