package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.util.Util;

public final class CondSpec<P extends OperationSpec<P, ?>> extends ColSpec<CondSpec<P>, P> implements Serializable {

    private static final long serialVersionUID = 328263884139551395L;
    
    private RowKey rowKey;
    private Value value;

    @Override
    protected CondSpec<P> self() { return this; }

    public RowKey getRowKey() {
        return this.rowKey;
    }
    public Value getValue() {
        return this.value;
    }

    public CondSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.rowKey = Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    public CondSpec<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }

    @Override
    protected String prepareStrRepHeadline() {
        return "[given-condition]";
    }
    @Override
    protected void prepareStrRepAdditional(final StringBuilder strGen) {
        if (this.rowKey != null) {
            Util.appendIndented(strGen, getDepth() + 1, "rowKey: ", this.rowKey, "\n");
        }
        if (this.value != null) {
            Util.appendIndented(strGen, getDepth() + 1, "value: ", this.value, "\n");
        }
    }

    public CondSpec(final P parent) {
        super(parent);
    }
}
