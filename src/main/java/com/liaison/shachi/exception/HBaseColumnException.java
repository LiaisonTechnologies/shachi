/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

import com.liaison.shachi.dto.ColRef;
import com.liaison.shachi.dto.RowRef;

public class HBaseColumnException extends HBaseTableRowException {
    
    private static final long serialVersionUID = 7523928044172073564L;
    
    private final ColRef colRef;
    
    public ColRef getColRef() {
        return this.colRef;
    }
    
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message) {
        super(rowRef, message);
        this.colRef = colRef;
    }
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause) {
        super(rowRef, message, cause);
        this.colRef = colRef;
    }
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, message, cause, enableSuppression, writableStackTrace);
        this.colRef = colRef;
    }
}
