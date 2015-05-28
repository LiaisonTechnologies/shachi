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

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseMalformedResultSetException extends HBaseColumnException {

    private static final long serialVersionUID = -3755663314725727956L;
    
    public HBaseMalformedResultSetException(RowSpec<?> rowSpec, ColSpec<?, ?> colSpec, String message) {
        super(rowSpec, colSpec, message);
    }
    public HBaseMalformedResultSetException(RowSpec<?> rowSpec, ColSpec<?, ?> colSpec, String message, Throwable cause) {
        super(rowSpec, colSpec, message, cause);
    }
    public HBaseMalformedResultSetException(RowSpec<?> rowSpec, ColSpec<?, ?> colSpec, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowSpec, colSpec, message, cause, enableSuppression, writableStackTrace);
    }
}
