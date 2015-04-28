package com.liaison.hbase.api.opspec;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.cnxn.HBaseContext;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseQueryInputValidationException;

public class NoOpSpec extends CRUDOperationSpec<NoOpSpec> {

    @Override
    public NoOpSpec self() {
        return this;
    }

    @Override
    protected final void validateInputs() throws HBaseQueryInputValidationException {
        // TODO Auto-generated method stub
    }
    @Override
    protected final OpResult executeOperation() throws HBaseException {
        // TODO Auto-generated method stub
        return null;
    }

    public NoOpSpec(final HBaseContext context) throws IllegalStateException {
        super(context);
    }
}
