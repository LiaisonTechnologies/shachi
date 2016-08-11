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

public class HBaseMultiColumnException extends HBaseTableRowException {
    
    private static final long serialVersionUID = 219076743239752424L;
    
    private final Iterable<? extends ColRef> colRefList;
    
    public final Iterable<? extends ColRef> getColRefList() {
        return this.colRefList;
    }
    
    public HBaseMultiColumnException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, String message) {
        super(rowRef, message);
        this.colRefList = colRefList;
    }
    public HBaseMultiColumnException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, String message, Throwable cause) {
        super(rowRef, message, cause);
        this.colRefList = colRefList;
    }
    public HBaseMultiColumnException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, message, cause, enableSuppression, writableStackTrace);
        this.colRefList = colRefList;
    }
}
