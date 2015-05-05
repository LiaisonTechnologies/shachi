package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.context.HBaseContext;

public final class NoOpSpec extends OperationSpec<NoOpSpec> implements Serializable {

    private static final long serialVersionUID = 1682509457085206829L;

    @Override
    public NoOpSpec self() { return this; }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:NONE]";
    }
    
    public NoOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
