/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.api.request.impl.ColSpec;
import com.liaison.hbase.api.request.impl.RowSpec;

public class HBaseNoCellException extends HBaseColumnException {
    
    private static final long serialVersionUID = 2043070353814422741L;
    
    public HBaseNoCellException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message) {
        super(rowSpec, colSpec, message);
    }
    public HBaseNoCellException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message, Throwable cause) {
        super(rowSpec, colSpec, message, cause);
    }
    public HBaseNoCellException(final RowSpec<?> rowSpec, final ColSpec<?,?> colSpec, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowSpec, colSpec, message, cause, enableSuppression, writableStackTrace);
    }
}
