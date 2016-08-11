/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

public class HBaseOpInputValidationException extends HBaseException {

    private static final long serialVersionUID = 583626977013787059L;
    
    public HBaseOpInputValidationException(String message) {
        super(message);
    }
    public HBaseOpInputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public HBaseOpInputValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
