package com.liaison.hbase.exception;

import com.liaison.hbase.util.Util;

public class HBaseRowQueryException extends HBaseException {

    private static final long serialVersionUID = -9220120682288168744L;
    
    private final byte[] rowKey;
    
    public byte[] getRowKey() {
        return this.rowKey;
    }
    
    public HBaseRowQueryException(final byte[] rowKey, final String message) {
        super(message);
        this.rowKey = Util.copyOf(rowKey);
    }
    public HBaseRowQueryException(final byte[] rowKey, final String message, final Throwable cause) {
        super(message, cause);
        this.rowKey = Util.copyOf(rowKey);
    }
    public HBaseRowQueryException(final byte[] rowKey, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.rowKey = Util.copyOf(rowKey);
    }
}
