package com.liaison.hbase;

import com.liaison.hbase.util.Util;

public class CriteriaSpec<P> {
    
    private P parent;
    
    public P done() {
        return this.parent;
    }
    
    public CriteriaSpec(final P parent) throws IllegalArgumentException {
        Util.ensureNotNull(parent, this, "parent");
    }
}
