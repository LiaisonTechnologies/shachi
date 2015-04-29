package com.liaison.hbase.api.opspec;

import com.liaison.hbase.util.AbstractSelfRef;
import com.liaison.hbase.util.Util;

public abstract class OperationSpec<O extends OperationSpec<O>> extends AbstractSelfRef<O> {
    private final OperationController parent;
    
    public final OperationController then() {
        return this.parent;
    }
    
    public OperationSpec(final OperationController parent) {
        Util.ensureNotNull(parent, this, "parent", OperationController.class);
        this.parent = parent;
    }
}
