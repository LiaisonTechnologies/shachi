package com.liaison.hbase.exception;

public class HBaseResourceManagementException extends HBaseException {

    private static final long serialVersionUID = -2284708688001161152L;
    
    public HBaseResourceManagementException(final String message) {
        super(message);
    }
    public HBaseResourceManagementException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseResourceManagementException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
