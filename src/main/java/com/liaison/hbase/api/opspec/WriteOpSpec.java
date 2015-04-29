package com.liaison.hbase.api.opspec;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseQueryInputValidationException;

public class WriteOpSpec extends CRUDOperationSpec<WriteOpSpec> {

    @Override
    public WriteOpSpec self() {
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

    public WriteOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
