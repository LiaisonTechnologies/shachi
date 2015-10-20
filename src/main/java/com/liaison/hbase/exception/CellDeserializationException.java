package com.liaison.hbase.exception;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 15:08
 */
public class CellDeserializationException extends HBaseRuntimeException {
    public CellDeserializationException(String message) {
        super(message);
    }
    public CellDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public CellDeserializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
