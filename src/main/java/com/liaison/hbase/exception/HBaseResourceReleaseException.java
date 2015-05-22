/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseResourceReleaseException extends HBaseResourceManagementException {

    private static final long serialVersionUID = 8410329137828683334L;
    
    public HBaseResourceReleaseException(final String message) {
        super(message);
    }
    public HBaseResourceReleaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public HBaseResourceReleaseException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
