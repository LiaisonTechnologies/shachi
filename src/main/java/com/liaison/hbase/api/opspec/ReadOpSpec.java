package com.liaison.hbase.api.opspec;

import org.apache.hadoop.hbase.client.Get;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.cnxn.HBaseContext;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseQueryInputValidationException;

public class ReadOpSpec extends CRUDOperationSpec<ReadOpSpec> {

    @Override
    public ReadOpSpec self() {
        return this;
    }

    @Override
    protected final void validateInputs() throws HBaseQueryInputValidationException {
        // TODO Auto-generated method stub
    }
    @Override
    protected final OpResult executeOperation() throws HBaseException {
        // TODO Auto-generated method stub
        final Get get;
        return null;
    }

    public ReadOpSpec(final HBaseContext context) throws IllegalStateException {
        super(context);
    }
}
