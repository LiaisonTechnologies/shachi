package com.liaison.hbase.api.opspec;

import com.liaison.hbase.context.HBaseContext;

public class UpdateOpSpec extends WriteOpSpec {

    @Override
    public UpdateOpSpec self() {
        return this;
    }
    
    public UpdateOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
