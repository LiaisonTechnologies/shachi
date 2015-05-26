/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api;

import com.liaison.hbase.api.opspec.WriteOpSpec;

public class WriteOpResult extends OpResult<WriteOpSpec> {

    private static final long serialVersionUID = -3656532804527415828L;

    public static class WriteOpResultBuilder extends OpResultBuilder<WriteOpSpec, WriteOpResult, WriteOpResultBuilder> {
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
