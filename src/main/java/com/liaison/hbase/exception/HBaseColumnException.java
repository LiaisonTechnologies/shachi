/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.api.opspec.ColSpec;
import com.liaison.hbase.api.opspec.RowSpec;

public class HBaseColumnException extends HBaseTableRowException {
    
    private static final long serialVersionUID = 7523928044172073564L;
    
    private final ColSpec<?,?> colSpec;
    
    public ColSpec<?,?> getColSpec() {
        return this.colSpec;
    }
    
    public HBaseColumnException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message) {
        super(rowSpec, message);
        this.colSpec = colSpec;
    }
    public HBaseColumnException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message, Throwable cause) {
        super(rowSpec, message, cause);
        this.colSpec = colSpec;
    }
    public HBaseColumnException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowSpec, message, cause, enableSuppression, writableStackTrace);
        this.colSpec = colSpec;
    }
}
