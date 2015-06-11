/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;



import com.liaison.commons.BytesUtil;
import com.liaison.commons.DefensiveCopyStrategy;
import com.liaison.commons.Util;

import java.io.Serializable;

public final class Datum extends Value implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;
    
    private static final String ENTITY_PREFIX_FOR_TOSTRING = "#";

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
        private Builder() throws IllegalArgumentException {
            super();
        }
    }
    
    public static Builder getDatumBuilder() {
        return new Builder();
    }
    public static final Datum of(final byte[] value, final long timestamp, final DefensiveCopyStrategy copyStrategy) {
        return getDatumBuilder().value(value, copyStrategy).ts(timestamp).build();
    }
    @Deprecated
    public static final Datum of(final byte[] value, final long timestamp) {
        return getDatumBuilder().value(value).ts(timestamp).build();
    }
    
    private final long ts;
    
    private Integer hc;
    private String strRep;
    
    public long getTS() {
        return ts;
    }

    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(super.hashCode() ^ Long.hashCode(this.ts));
        }
        return this.hc.intValue();
    }
    @Override
    public boolean equals(final Object otherObj) {
        final Datum otherDatum;
        if (otherObj instanceof Datum) {
            otherDatum = (Datum) otherObj;
            return (super.equals(otherObj) && (this.ts == otherDatum.ts));
        }
        return false;
    }
    @Override
    public String toString() {
        if (this.strRep == null) {
            this.strRep =
                buildStrRep(ENTITY_PREFIX_FOR_TOSTRING, (strGen) -> {
                    strGen.append(BytesUtil.toString(getValue(DefensiveCopyStrategy.NEVER)));
                    strGen.append("(@");
                    strGen.append(this.ts);
                    strGen.append(")");
                });
        }
        return this.strRep;
    }
    
    private Datum(final Builder build) throws IllegalArgumentException {
        super(build);
        Util.ensureNotNull(build.value, this, "value", byte[].class);
        Util.ensureNotNull(build.tsObj, this, "tsObj", Long.class);
        this.ts = build.tsObj.longValue();
    }
}
