/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.exception;

import com.liaison.hbase.api.request.impl.SpecState;
import com.liaison.hbase.api.request.impl.StatefulSpec;

public class SpecValidationException extends IllegalStateException {

    private static final long serialVersionUID = -3339785194180457026L;
    
    private final SpecState stateStart;
    private final SpecState stateEnd;
    private final StatefulSpec<?, ?> spec;
    
    public SpecState getStateStart() {
        return this.stateStart;
    }
    public SpecState getStateEnd() {
        return this.stateEnd;
    }
    public StatefulSpec<?, ?> getSpec() {
        return this.spec;
    }
    
    private static String generateMessage(final SpecState stateStart, final SpecState stateEnd, final StatefulSpec<?, ?> spec, final String message, final Throwable cause) {
        final StringBuilder strGen;
        strGen = new StringBuilder();
        strGen.append("Failed to transition spec ");
        strGen.append(spec);
        strGen.append(" from state ");
        strGen.append(stateStart);
        strGen.append(" to state ");
        strGen.append(stateEnd);
        strGen.append(": ");
        strGen.append(message);
        if (cause != null) {
            strGen.append("; ");
            strGen.append(cause);
        }
        return strGen.toString();
    }
    private static String generateMessage(final SpecState stateStart, final SpecState stateEnd, final StatefulSpec<?, ?> spec, final String message) {
        return generateMessage(stateStart, stateEnd, spec, message, null);
    }
    
    public SpecValidationException(final SpecState stateStart, final SpecState stateEnd, final StatefulSpec<?, ?> spec, final String message) {
        super(generateMessage(stateStart, stateEnd, spec, message));
        this.stateStart = stateStart;
        this.stateEnd = stateEnd;
        this.spec = spec;
    }
    public SpecValidationException(final SpecState stateStart, final SpecState stateEnd, final StatefulSpec<?, ?> spec, final String message, final Throwable cause) {
        super(generateMessage(stateStart, stateEnd, spec, message, cause), cause);
        this.stateStart = stateStart;
        this.stateEnd = stateEnd;
        this.spec = spec;
    }
}
