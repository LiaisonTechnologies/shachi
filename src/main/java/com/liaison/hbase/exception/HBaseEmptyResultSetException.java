/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.api.request.frozen.ColSpecFrozen;
import com.liaison.hbase.api.request.impl.RowSpec;

public class HBaseEmptyResultSetException extends HBaseMultiColumnException {
    
    private static final long serialVersionUID = 1935706509279956966L;
    
    public HBaseEmptyResultSetException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpecFrozen> colSpecList, final String message) {
        super(rowSpec, colSpecList, message);
    }
    public HBaseEmptyResultSetException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpecFrozen> colSpecList, final String message, final Throwable cause) {
        super(rowSpec, colSpecList, message, cause);
    }
    public HBaseEmptyResultSetException(final RowSpec<?> rowSpec, final Iterable<? extends ColSpecFrozen> colSpecList, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(rowSpec, colSpecList, message, cause, enableSuppression, writableStackTrace);
    }
}
