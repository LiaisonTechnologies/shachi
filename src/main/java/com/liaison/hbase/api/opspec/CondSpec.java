package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.util.Util;

public final class CondSpec<P extends OperationSpec<P>> extends ColSpec<CondSpec<P>, P> {

    private RowKey rowKey;
    private Value value;

    @Override
    protected CondSpec<P> self() { return this; }

    public CondSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        this.rowKey = Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    public CondSpec<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }

    public CondSpec(final P parent) {
        super(parent);
    }
}
