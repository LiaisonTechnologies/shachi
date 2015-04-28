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

public class HBaseEmptyResultSetException extends HBaseMultiColumnException {
    
    private static final long serialVersionUID = 8534370542477276807L;
    
    public HBaseEmptyResultSetException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, final String message) {
        super(rowKey, fqpList, message);
    }
    public HBaseEmptyResultSetException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, final String message, final Throwable cause) {
        super(rowKey, fqpList, message, cause);
    }
    public HBaseEmptyResultSetException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(rowKey, fqpList, message, cause, enableSuppression, writableStackTrace);
    }
}
