package com.liaison.hbase.api.opspec;

import com.liaison.hbase.cnxn.HBaseContext;

public class CreateOpSpec extends WriteOpSpec {

    @Override
    public CreateOpSpec self() {
        return this;
    }
    
    public CreateOpSpec(final HBaseContext context) throws IllegalStateException {
        super(context);
    }
}
