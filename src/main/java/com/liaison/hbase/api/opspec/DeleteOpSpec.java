package com.liaison.hbase.api.opspec;

import com.liaison.hbase.cnxn.HBaseContext;

public class DeleteOpSpec extends WriteOpSpec {

    @Override
    public DeleteOpSpec self() {
        return this;
    }
    
    public DeleteOpSpec(final HBaseContext context) throws IllegalStateException {
        super(context);
    }

}
