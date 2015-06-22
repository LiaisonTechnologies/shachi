/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

public class HBaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8921980270867671132L;
    
    public HBaseRuntimeException(final String message) {
        super(message);
    }
    public HBaseRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseRuntimeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
