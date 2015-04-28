package com.liaison.hbase.exception;

public class HBaseException extends Exception {

    private static final long serialVersionUID = -1572117727793745686L;

    public HBaseException(String message) {
        super(message);
    }
    public HBaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
