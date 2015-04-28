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

import com.liaison.hbase.util.AbstractSelfRefBuilder;
import com.liaison.hbase.util.Util;

public class Value implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;

    protected abstract static class AbstractValueBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        protected byte[] value;
        public B value(final byte[] value) {
            this.value = value;
            return self();
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
        private Builder() {}
    }
    
    public static Builder getValueBuilder() {
        return new Builder();
    }
    public static final Value of(byte[] value) {
        return getValueBuilder().value(value).build();
    }
    
    private final byte[] value;
    
    public byte[] getValue() {
        return value;
    }

    // TODO equals, toString, hashCode, etc.
    
    protected Value(final AbstractValueBuilder<?,?> build) throws IllegalArgumentException {
        Util.ensureNotNull(build.value, this, "value", byte[].class);
        this.value = build.value;
    }
    
    public static void main(String[] arguments) {
    }
}
