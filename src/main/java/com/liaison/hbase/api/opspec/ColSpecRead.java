package com.liaison.hbase.api.opspec;

public final class ColSpecRead<P extends OperationSpec<P>> extends ColSpec<ColSpecRead<P>, P> {

    @Override
    protected ColSpecRead<P> self() { return this; }

    public ColSpecRead(final P parent) {
        super(parent);
    }
}
