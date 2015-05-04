package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import org.apache.hadoop.hbase.client.Operation;

import com.liaison.hbase.context.HBaseContext;

public final class NoOpSpec extends OperationSpec<NoOpSpec, Operation> implements Serializable {

    private static final long serialVersionUID = 1682509457085206829L;

    @Override
    public NoOpSpec self() { return this; }

    @Override
    protected Operation buildHBaseOp() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(NoOpSpec.class.getSimpleName()
                                                + " does not correspond to any HBase "
                                                + Operation.class.getSimpleName());
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        
    }
    
    public NoOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
