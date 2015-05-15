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
    
    public static WriteOpResultBuilder getBuilder() {
        return new WriteOpResultBuilder();
    }
    
    private final boolean mutationPerformed;
    
    public boolean isMutationPerformed() {
        return this.mutationPerformed;
    }
    
    private WriteOpResult(final WriteOpResultBuilder build) {
        super(build);
        this.mutationPerformed = build.mutationPerformed;
    }
}
