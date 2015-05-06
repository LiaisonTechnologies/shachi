package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.Util;

public abstract class OperationSpec<O extends OperationSpec<O>> extends StatefulSpec<O, OperationController> implements Serializable {
    
    private static final long serialVersionUID = 5533663131351737507L;
    
    private final HBaseContext context;
    
    protected HBaseContext getContext() {
        return this.context;
    }
    
    public final OperationController then() throws SpecValidationException {
        freezeRecursive();
        return getParent();
    }
    
    public OperationSpec(final HBaseContext context, final OperationController parent) {
        super(parent);
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
    }
}
