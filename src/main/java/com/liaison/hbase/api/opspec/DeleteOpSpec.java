package com.liaison.hbase.api.opspec;

import com.liaison.hbase.context.HBaseContext;

public class DeleteOpSpec extends WriteOpSpec {

    @Override
    public DeleteOpSpec self() {
        return this;
    }
    
    public DeleteOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }

}
