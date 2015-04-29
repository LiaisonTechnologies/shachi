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
import com.liaison.hbase.util.Util;

public final class Datum extends Value implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;

    public static final class Builder extends AbstractValueBuilder<Datum, Builder> {
        private Long tsObj;
        public Builder ts(final long ts) {
            this.tsObj = Long.valueOf(ts);
            return this;
        }
        @Override
        public Builder self() {
            return this;
        }
        @Override
        public Datum build() {
            return new Datum(this);
        }
        private Builder(final HBaseContext context) throws IllegalArgumentException {
            super(context);
        }
    }
    
    public static Builder getDatumBuilder(final HBaseContext context) {
        return new Builder(context);
    }
    public static final Datum of(final byte[] value, final long timestamp, final HBaseContext context) {
        return getDatumBuilder(context).value(value).ts(timestamp).build();
    }
    
    private final long tS;
    
    public long getTS() {
        return tS;
    }

    // TODO equals, toString, hashCode, etc.
    
    private Datum(final Builder build) throws IllegalArgumentException {
        super(build);
        Util.ensureNotNull(build.value, this, "value", byte[].class);
        Util.ensureNotNull(build.tsObj, this, "tsObj", Long.class);
        this.tS = build.tsObj.longValue();
    }
}
