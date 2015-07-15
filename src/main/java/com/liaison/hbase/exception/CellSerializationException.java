package com.liaison.hbase.exception;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 15:07
 */
public class CellSerializationException extends HBaseRuntimeException {
    public CellSerializationException(String message) {
        super(message);
    }
    public CellSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public CellSerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
