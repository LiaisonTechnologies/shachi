package com.liaison.hbase.api.opspec;

import com.liaison.hbase.api.OpResult;

public class NoOpSpec extends CRUDOperationSpec<NoOpSpec> {

    @Override
    public NoOpSpec self() {
        return this;
    }

    @Override
    public OpResult exec() {
        return null;
    }

}
