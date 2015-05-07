/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.dto.RowKey;

public class HBaseColumnException extends HBaseRowQueryException {
    
    private static final long serialVersionUID = 7523928044172073564L;
    
    private final FamilyQualifierPair fqp;
    
    public FamilyQualifierPair getFqp() {
        return this.fqp;
    }
    
    public HBaseColumnException(final RowKey rowKey, final FamilyQualifierPair fqp, String message) {
        super(rowKey, message);
        this.fqp = fqp;
    }
    public HBaseColumnException(final RowKey rowKey, final FamilyQualifierPair fqp, String message, Throwable cause) {
        super(rowKey, message, cause);
        this.fqp = fqp;
    }
    public HBaseColumnException(final RowKey rowKey, final FamilyQualifierPair fqp, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowKey, message, cause, enableSuppression, writableStackTrace);
        this.fqp = fqp;
    }
}
