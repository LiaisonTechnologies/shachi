package com.liaison.hbase.exception;

import com.liaison.hbase.api.opspec.RowSpec;

public class HBaseTableRowException extends HBaseException {

    private static final long serialVersionUID = -9220120682288168744L;
    
    private final RowSpec<?> tableRow;
    
    public RowSpec<?> getTableRow() {
        return this.tableRow;
    }
    
    public HBaseTableRowException(final RowSpec<?> tableRow, final String message) {
        super(message);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowSpec<?> tableRow, final String message, final Throwable cause) {
        super(message, cause);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowSpec<?> tableRow, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.tableRow = tableRow;
    }
}
