package com.liaison.hbase.api.opspec;

import com.liaison.hbase.util.Util;

public class CriteriaSpec<P> {
    
    private P parent;
    
    public P up() {
        return this.parent;
    }
    
    public CriteriaSpec(final P parent) throws IllegalArgumentException {
        Util.ensureNotNull(parent, this, "parent");
    }
}
