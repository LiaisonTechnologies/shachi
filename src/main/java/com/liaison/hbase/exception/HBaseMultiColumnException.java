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

public class HBaseMultiColumnException extends HBaseRowQueryException {
    
    private static final long serialVersionUID = 7196636404982884264L;
    
    private final Iterable<FamilyQualifierPair> fqpList;
    
    public Iterable<FamilyQualifierPair> getFqpList() {
        return this.fqpList;
    }
    
    public HBaseMultiColumnException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, String message) {
        super(rowKey, message);
        this.fqpList = fqpList;
    }
    public HBaseMultiColumnException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, String message, Throwable cause) {
        super(rowKey, message, cause);
        this.fqpList = fqpList;
    }
    public HBaseMultiColumnException(final byte[] rowKey, final Iterable<FamilyQualifierPair> fqpList, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowKey, message, cause, enableSuppression, writableStackTrace);
        this.fqpList = fqpList;
    }
}
