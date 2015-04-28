package com.liaison.hbase.api.opspec;

import com.liaison.hbase.cnxn.HBaseContext;

public class UpdateOpSpec extends WriteOpSpec {

    @Override
    public UpdateOpSpec self() {
        return this;
    }
    
    public UpdateOpSpec(final HBaseContext context) throws IllegalStateException {
        super(context);
    }
}
