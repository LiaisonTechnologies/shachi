/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.exception;

import com.liaison.shachi.api.request.impl.SpecState;
import com.liaison.shachi.api.request.impl.StatefulSpec;

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
