/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.dto.RowRef;

public class HBaseTableRowException extends HBaseException {

    private static final long serialVersionUID = -9220120682288168744L;
    
    private final RowRef tableRow;
    
    public RowRef getTableRow() {
        return this.tableRow;
    }
    
    public HBaseTableRowException(final RowRef tableRow, final String message) {
        super(message);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowRef tableRow, final String message, final Throwable cause) {
        super(message, cause);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowRef tableRow, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.tableRow = tableRow;
    }
}
