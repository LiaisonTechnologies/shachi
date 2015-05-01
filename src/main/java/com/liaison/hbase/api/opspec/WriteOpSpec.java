package com.liaison.hbase.api.opspec;

import org.apache.hadoop.hbase.client.Put;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseOpInputValidationException;

public class WriteOpSpec extends CRUDOperationSpec<Put, WriteOpSpec> {
    
    @Override
    public WriteOpSpec self() {
        return this;
    }

    @Override
    public Put buildHBaseOp() throws HBaseOpInputValidationException {
        final Put put;
        
        put = new Put(row().getValue());
        
    }

    public WriteOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
