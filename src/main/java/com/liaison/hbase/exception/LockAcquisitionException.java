/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

public class LockAcquisitionException extends Exception {

    private static final long serialVersionUID = -1794739182736671947L;
    
    public LockAcquisitionException() {
    }
    public LockAcquisitionException(String message) {
        super(message);
    }
    public LockAcquisitionException(Throwable cause) {
        super(cause);
    }
    public LockAcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
    public LockAcquisitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
