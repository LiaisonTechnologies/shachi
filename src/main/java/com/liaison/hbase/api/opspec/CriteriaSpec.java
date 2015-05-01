package com.liaison.hbase.api.opspec;

import com.liaison.hbase.util.AbstractSelfRef;
import com.liaison.hbase.util.Util;

public abstract class CriteriaSpec<C extends CriteriaSpec<C, P>, P> extends AbstractSelfRef<C> {
    
    private P parent;
    
    public P and() {
        return this.parent;
    }
    
    public CriteriaSpec(final P parent) throws IllegalArgumentException {
        Util.ensureNotNull(parent, this, "parent");
    }
}
