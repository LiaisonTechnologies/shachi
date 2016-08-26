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
package com.liaison.shachi.api.response;

import com.liaison.shachi.api.request.impl.WriteOpSpecDefault;

public class WriteOpResult extends OpResult<WriteOpSpecDefault> {

    private static final long serialVersionUID = -3656532804527415828L;

    public static class WriteOpResultBuilder extends OpResultBuilder<WriteOpSpecDefault, WriteOpResult, WriteOpResultBuilder> {
        private boolean mutationPerformed;
        
        @Override
        public final WriteOpResultBuilder self() {
            return this;
        }
        
        public WriteOpResultBuilder mutationPerformed(final boolean mutationPerformed) {
            this.mutationPerformed = mutationPerformed;
            return self();
        }
        
        @Override
        public final WriteOpResult build() {
            return new WriteOpResult(this);
        }
        
        private WriteOpResultBuilder() {
            super();
            this.mutationPerformed = false;
        }
    }
    
    private static final String OPRESULT_TYPE_STR = "WRITE";
    
    public static WriteOpResultBuilder getBuilder() {
        return new WriteOpResultBuilder();
    }
    
    private final boolean mutationPerformed;
    
    public boolean isMutationPerformed() {
        return this.mutationPerformed;
    }
    
    @Override
    protected boolean deepEquals(OpResult<?> otherOpResult) {
        final WriteOpResult otherWriteOpResult;
        if (otherOpResult instanceof ReadOpResult) {
            otherWriteOpResult = (WriteOpResult) otherOpResult;
            return (this.mutationPerformed == otherWriteOpResult.mutationPerformed);
        }
        return false;
    }
    
    @Override
    protected String getOpResultTypeStr() {
        return OPRESULT_TYPE_STR;
    }
    
    @Override
    protected void prepareStrRepAdditional(StringBuilder strGen) {
        strGen.append("{written=");
        strGen.append(this.mutationPerformed);
        strGen.append("}");
    }
    
    private WriteOpResult(final WriteOpResultBuilder build) {
        super(build);
        this.mutationPerformed = build.mutationPerformed;
    }
}
