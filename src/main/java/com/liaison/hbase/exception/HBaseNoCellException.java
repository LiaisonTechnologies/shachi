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

public class HBaseNoCellException extends HBaseColumnException {
    
    private static final long serialVersionUID = 2043070353814422741L;
    
    public HBaseNoCellException(final RowRef rowRef, final ColRef colRef, String message) {
        super(rowRef, colRef, message);
    }
    public HBaseNoCellException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause) {
        super(rowRef, colRef, message, cause);
    }
    public HBaseNoCellException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, colRef, message, cause, enableSuppression, writableStackTrace);
    }
}
