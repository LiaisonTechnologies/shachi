/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.serialization.BytesUtil;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;

import java.io.Serializable;

public final class Datum extends Value implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;
    
    private static final String ENTITY_PREFIX_FOR_TOSTRING = "#";

    public static final class Builder extends AbstractValueBuilder<Datum, Builder> {
        private Long tsObj;
        private Long version;
        public Builder ts(final long ts) {
            this.tsObj = Long.valueOf(ts);
            return this;
        }
        public Builder version(final long version) {
            this.version = Long.valueOf(version);
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
            this.tsObj = null;
            this.version = null;
        }
    }
    
    public static Builder with() {
        return new Builder();
    }
    public static final Datum of(final byte[] value, final long timestamp, final long version, final DefensiveCopyStrategy copyStrategy) {
        return with().value(value, copyStrategy).ts(timestamp).version(version).build();
    }
    @Deprecated
    public static final Datum of(final byte[] value, final long timestamp, final long version) {
        return with().value(value).ts(timestamp).version(version).build();
    }
    public static final Datum of(final byte[] value, final long timestamp, final DefensiveCopyStrategy copyStrategy) {
        return with().value(value, copyStrategy).ts(timestamp).build();
    }
    @Deprecated
    public static final Datum of(final byte[] value, final long timestamp) {
        return with().value(value).ts(timestamp).build();
    }
    
    private final long ts;
    /**
     * The version number as stored in HBase, populated IFF the corresponding model/schema
     * specifies a versioning strategy. If the model does not specify versioning, then this field
     * will be null.
     */
    private final Long version;
    
    private Integer hc;
    private String strRep;
    
    public long getTS() {
        return ts;
    }

    /**
     * Returns the version number as stored in HBase, populated IFF the corresponding model/schema
     * specifies a versioning strategy. If the model does not specify versioning, then this field
     * will be null.
     * @return the column version number, if the corresponding model configures a versioning scheme
     */
    public Long getVersion() {
        return this.version;
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
        if (this == otherObj) {
            return true;
        }
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
                    strGen.append("(");
                    if (this.version != null) {
                        strGen.append("ver=");
                        strGen.append(this.version);
                        strGen.append(",");
                    }
                    strGen.append("ts=");
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
        this.version = build.version;
    }
}
