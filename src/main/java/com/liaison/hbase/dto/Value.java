/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;

import java.io.Serializable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.AbstractSelfRefBuilder;
import com.liaison.hbase.util.Util;

public class Value implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;

    protected abstract static class AbstractValueBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        protected final HBaseContext context;
        protected byte[] value;
        public B value(final byte[] value) {
            this.value = Util.setWithContext(value, this.context);
            return self();
        }
        protected AbstractValueBuilder(final HBaseContext context) throws IllegalArgumentException {
            Util.ensureNotNull(context, this, "context", HBaseContext.class);
            this.context = context;
            this.value = null;
        }
    }
    public static class Builder extends AbstractValueBuilder<Value, Builder> {
        @Override
        protected Builder self() {
            return this;
        }
        @Override
        public Value build() {
            return new Value(self());
        }
        private Builder(final HBaseContext context) throws IllegalArgumentException {
            super(context);
        }
    }
    
    public static Builder getValueBuilder(final HBaseContext context) {
        return new Builder(context);
    }
    public static Value of(final byte[] value, final HBaseContext context) {
        return getValueBuilder(context).value(value).build();
    }
    
    private final HBaseContext context;
    private final byte[] value;
    
    public byte[] getValue() {
        return Util.getWithContext(this.value, this.context);
    }

    // TODO equals, toString, hashCode, etc.
    
    protected Value(final AbstractValueBuilder<?,?> build) throws IllegalArgumentException {
        Util.ensureNotNull(build.context, this, "context", HBaseContext.class);
        Util.ensureNotNull(build.value, this, "value", byte[].class);
        this.context = build.context;
        this.value = build.value;
    }
    
    public static void main(String[] arguments) {
    }
}
