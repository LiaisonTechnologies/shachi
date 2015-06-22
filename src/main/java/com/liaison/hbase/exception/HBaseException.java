/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

public class HBaseException extends Exception {

    private static final long serialVersionUID = -1572117727793745686L;

    public HBaseException(String message) {
        super(message);
    }
    public HBaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
