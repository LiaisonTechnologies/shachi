/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import java.io.IOException;

public class NonTerminalClosureException extends IOException {

    private static final long serialVersionUID = 2162945182147970019L;
    
    public NonTerminalClosureException(String message) {
        super(message);
    }
    public NonTerminalClosureException(String message, Throwable cause) {
        super(message, cause);
    }
}
