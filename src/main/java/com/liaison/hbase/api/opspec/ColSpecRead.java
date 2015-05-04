package com.liaison.hbase.api.opspec;

import java.io.Serializable;

public final class ColSpecRead<P extends OperationSpec<P, ?>> extends ColSpec<ColSpecRead<P>, P> implements Serializable {

    private static final long serialVersionUID = -3480030817298140795L;

    @Override
    protected ColSpecRead<P> self() { return this; }

    @Override
    protected String prepareStrRepHeadline() {
        return "[from-column]";
    }
    
    public ColSpecRead(final P parent) {
        super(parent);
    }
}
