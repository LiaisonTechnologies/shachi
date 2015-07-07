/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.dto.ColRef;
import com.liaison.hbase.dto.RowRef;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseMalformedResultSetException extends HBaseColumnException {

    private static final long serialVersionUID = -3755663314725727956L;
    
    public HBaseMalformedResultSetException(RowRef rowRef, ColRef colRef, String message) {
        super(rowRef, colRef, message);
    }
    public HBaseMalformedResultSetException(RowRef rowRef, ColRef colRef, String message, Throwable cause) {
        super(rowRef, colRef, message, cause);
    }
    public HBaseMalformedResultSetException(RowRef rowRef, ColRef colRef, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, colRef, message, cause, enableSuppression, writableStackTrace);
    }
}
