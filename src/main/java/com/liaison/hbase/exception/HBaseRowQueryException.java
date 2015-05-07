package com.liaison.hbase.exception;

import com.liaison.hbase.dto.RowKey;

public class HBaseRowQueryException extends HBaseException {

    private static final long serialVersionUID = -9220120682288168744L;
    
    private final RowKey rowKey;
    
    public RowKey getRowKey() {
        return this.rowKey;
    }
    
    public HBaseRowQueryException(final RowKey rowKey, final String message) {
        super(message);
        this.rowKey = rowKey;
    }
    public HBaseRowQueryException(final RowKey rowKey, final String message, final Throwable cause) {
        super(message, cause);
        this.rowKey = rowKey;
    }
    public HBaseRowQueryException(final RowKey rowKey, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.rowKey = rowKey;
    }
}
