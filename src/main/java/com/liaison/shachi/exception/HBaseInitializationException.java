/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

public class HBaseInitializationException extends HBaseRuntimeException {

    private static final long serialVersionUID = 4050120586274690725L;
    
    public HBaseInitializationException(final String message) {
        super(message);
    }
    public HBaseInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseInitializationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
