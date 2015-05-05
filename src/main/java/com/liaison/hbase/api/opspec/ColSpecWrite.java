package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.util.Util;

public final class ColSpecWrite<P extends OperationSpec<P, ?>> extends ColSpec<ColSpecWrite<P>, P> implements Serializable {

    private static final long serialVersionUID = -194106227851821468L;
    
    private Long ts;
    private Value value;
    
    @Override
    protected ColSpecWrite<P> self() { return this; }

    public Long getTs() {
        return this.ts;
    }
    public Value getValue() {
        return this.value;
    }

    public ColSpecWrite<P> ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.ts = Util.validateExactlyOnceParam(Long.valueOf(ts), this, "ts", Long.class, this.ts);
        return self();
    }
    public ColSpecWrite<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }

    @Override
    protected String prepareStrRepHeadline() {
        return "[to-column]";
    }
    @Override
    protected void prepareStrRepAdditional(final StringBuilder strGen) {
        if (this.value != null) {
            Util.appendIndented(strGen, getDepth() + 1, "value: ", this.value, "\n");
        }
        if (this.ts != null) {
            Util.appendIndented(strGen, getDepth() + 1, "ts: ", this.ts, "\n");
        }
    }
    
    public ColSpecWrite(final P parent) {
        super(parent);
    }
}
