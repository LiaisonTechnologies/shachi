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

public final class RowKey extends Value implements Serializable {

    private static final long serialVersionUID = 7786225976018409474L;

    public static final class Builder extends AbstractValueBuilder<RowKey, Builder> {
        @Override
        public Builder self() {
            return this;
        }
        @Override
        public RowKey build() {
            return new RowKey(this);
        }
        private Builder(final HBaseContext context) throws IllegalArgumentException {
            super(context);
        }
    }
    
    public static Builder getRowKeyBuilder(final HBaseContext context) {
        return new Builder(context);
    }
    public static final RowKey rowOf(byte[] value, final HBaseContext context) {
        return getRowKeyBuilder(context).value(value).build();
    }

    // TODO equals, toString, hashCode, etc.
    
    private RowKey(final Builder build) throws IllegalArgumentException {
        super(build);
    }
}
