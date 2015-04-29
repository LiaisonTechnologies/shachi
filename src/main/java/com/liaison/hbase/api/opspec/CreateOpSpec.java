package com.liaison.hbase.api.opspec;

import com.liaison.hbase.context.HBaseContext;

public class CreateOpSpec extends WriteOpSpec {

    @Override
    public CreateOpSpec self() {
        return this;
    }
    
    public CreateOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
