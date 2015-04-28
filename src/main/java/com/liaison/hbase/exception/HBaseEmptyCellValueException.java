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

public class HBaseEmptyCellValueException extends HBaseColumnException {
    
    private static final long serialVersionUID = 8167381539562556543L;
    
    public HBaseEmptyCellValueException(final byte[] rowKey, final FamilyQualifierPair fqp, String message) {
        super(rowKey, fqp, message);
    }
    public HBaseEmptyCellValueException(final byte[] rowKey, final FamilyQualifierPair fqp, String message, Throwable cause) {
        super(rowKey, fqp, message, cause);
    }
    public HBaseEmptyCellValueException(final byte[] rowKey, final FamilyQualifierPair fqp, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowKey, fqp, message, cause, enableSuppression, writableStackTrace);
    }
}
