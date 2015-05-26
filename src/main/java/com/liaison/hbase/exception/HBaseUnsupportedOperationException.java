/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

public class HBaseUnsupportedOperationException extends HBaseException {

    private static final long serialVersionUID = -7243882672508779618L;
    
    public HBaseUnsupportedOperationException(String message) {
        super(message);
    }
    public HBaseUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseUnsupportedOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
