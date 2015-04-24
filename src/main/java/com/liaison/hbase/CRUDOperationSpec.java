package com.liaison.hbase;

public abstract class CRUDOperationSpec {
    
    private LongValueSpec<CRUDOperationSpec> tsSpec;
    
    public abstract OpResult exec();
    
    public LongValueSpec<CRUDOperationSpec> ts() {
        this.tsSpec = new LongValueSpec<>(this);
        return this.tsSpec;
    }
    
    public CRUDOperationSpec() {
        this.tsSpec = null;
    }
}
