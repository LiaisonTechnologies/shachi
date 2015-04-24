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

import com.liaison.hbase.util.Util;

public final class Datum implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;

    public static final class Builder {
        private byte[] value;
        private Long tsObj;
        
        public Builder value(final byte[] value) {
            this.value = value;
            return this;
        }
        public Builder ts(final long ts) {
            this.tsObj = Long.valueOf(ts);
            return this;
        }
        public Datum build() {
            return new Datum(this);
        }
        private Builder() {}
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    private final byte[] value;
    private final long tS;
    
    public byte[] getValue() {
        return value;
    }
    public long getTS() {
        return tS;
    }

    // TODO equals, toString, hashCode, etc.
    
    private Datum(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.value, this, "value", byte[].class);
        Util.ensureNotNull(build.tsObj, this, "tsObj", Long.class);
        this.value = build.value;
        this.tS = build.tsObj.longValue();
    }
}
