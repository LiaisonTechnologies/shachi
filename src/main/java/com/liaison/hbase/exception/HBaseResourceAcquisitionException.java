package com.liaison.hbase.exception;

public class HBaseResourceAcquisitionException extends HBaseResourceManagementException {

    private static final long serialVersionUID = -1543761011363233921L;

    public HBaseResourceAcquisitionException(final String message) {
        super(message);
    }
    public HBaseResourceAcquisitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseResourceAcquisitionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
