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

public class HBaseEmptyResultSetException extends HBaseMultiColumnException {
    
    private static final long serialVersionUID = 1935706509279956966L;
    
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message) {
        super(rowRef, colRefList, message);
    }
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message, final Throwable cause) {
        super(rowRef, colRefList, message, cause);
    }
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(rowRef, colRefList, message, cause, enableSuppression, writableStackTrace);
    }
}
