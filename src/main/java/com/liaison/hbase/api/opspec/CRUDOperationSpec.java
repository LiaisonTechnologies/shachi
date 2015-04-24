package com.liaison.hbase.api.opspec;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.api.RowKey;

public abstract class CRUDOperationSpec<O extends CRUDOperationSpec<O>> {
    
    private RowKey rowKey; 
    private LongValueSpec<O> tsSpec;
    
    public abstract O self();
    public abstract OpResult exec();
    
    public O rowKey(final RowKey rowKey) {
        this.rowKey = rowKey;
        return self();
    }
    
    public LongValueSpec<O> ts() {
        this.tsSpec = new LongValueSpec<O>(self());
        return this.tsSpec;
    }
    
    public CRUDOperationSpec() throws IllegalStateException {
        this.tsSpec = null;
        if (self() != this) {
            throw new IllegalStateException("self() method implementation must return identity");
        }
    }
}
