package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import org.apache.hadoop.hbase.client.Operation;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public abstract class OperationSpec<O extends OperationSpec<O, H>, H extends Operation> extends StatefulSpec<O, OperationController> implements Serializable {
    
    private static final long serialVersionUID = 5533663131351737507L;
    
    private final HBaseContext context;
    
    protected abstract H buildHBaseOp();
    
    protected HBaseContext getContext() {
        return this.context;
    }
    
    public final OperationController then() {
        return getParent();
    }
    
    public OperationSpec(final HBaseContext context, final OperationController parent) {
        super(parent);
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
    }
}
