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

import com.liaison.hbase.util.DefensiveCopyStrategy;

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
        private Builder() throws IllegalArgumentException {
            super();
        }
    }
    
    public static Builder getRowKeyBuilder() {
        return new Builder();
    }
    public static final RowKey of(byte[] value, final DefensiveCopyStrategy copyStrategy) {
        return getRowKeyBuilder().value(value, copyStrategy).build();
    }
    @Deprecated
    public static final RowKey of(byte[] value) {
        return getRowKeyBuilder().value(value).build();
    }

    // TODO equals, toString, hashCode, etc.
    
    private RowKey(final Builder build) throws IllegalArgumentException {
        super(build);
    }
}
