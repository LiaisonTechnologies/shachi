package com.liaison.hbase.exception;

import com.liaison.hbase.api.opspec.SpecState;
import com.liaison.hbase.api.opspec.StatefulSpec;

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
