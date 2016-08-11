/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

import java.io.IOException;

public class TerminalClosureException extends IOException {
    
    private static final long serialVersionUID = 2345355415265665939L;
    
    public TerminalClosureException(String message) {
        super(message);
    }
    public TerminalClosureException(String message, Throwable cause) {
        super(message, cause);
    }
}
