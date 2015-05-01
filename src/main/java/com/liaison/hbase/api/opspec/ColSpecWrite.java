package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.util.Util;

public final class ColSpecWrite<P extends OperationSpec<P>> extends ColSpec<ColSpecWrite<P>, P> {

    private Long ts;
    private Value value;
    
    @Override
    protected ColSpecWrite<P> self() { return this; }

    public ColSpecWrite<P> ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        this.ts = Util.validateExactlyOnceParam(Long.valueOf(ts), this, "ts", Long.class, this.ts);
        return self();
    }
    public ColSpecWrite<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }
    
    public ColSpecWrite(final P parent) {
        super(parent);
    }
}
