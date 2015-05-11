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

public class HBaseMultiColumnException extends HBaseTableRowException {
    
    private static final long serialVersionUID = 219076743239752424L;
    
    private final Iterable<? extends ColSpec<?,?>> colSpecList;
    
    public final Iterable<? extends ColSpec<?,?>> getColSpecList() {
        return this.colSpecList;
    }
    
    public HBaseMultiColumnException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpec<?,?>> colSpecList, String message) {
        super(rowSpec, message);
        this.colSpecList = colSpecList;
    }
    public HBaseMultiColumnException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpec<?,?>> colSpecList, String message, Throwable cause) {
        super(rowSpec, message, cause);
        this.colSpecList = colSpecList;
    }
    public HBaseMultiColumnException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpec<?,?>> colSpecList, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowSpec, message, cause, enableSuppression, writableStackTrace);
        this.colSpecList = colSpecList;
    }
}
