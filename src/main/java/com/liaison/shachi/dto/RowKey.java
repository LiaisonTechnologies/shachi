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

import com.liaison.javabasics.serialization.BytesUtil;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;

import java.io.Serializable;
import java.nio.charset.Charset;

public final class RowKey extends Value implements Serializable {

    private static final long serialVersionUID = 7786225976018409474L;
    
    private static final String ENTITY_PREFIX_FOR_TOSTRING = "*";

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
    @Deprecated
    public static final RowKey of(final byte[] value) {
        return getRowKeyBuilder().value(value).build();
    }
    public static final RowKey of(final byte[] value, final DefensiveCopyStrategy copyStrategy) {
        return getRowKeyBuilder().value(value, copyStrategy).build();
    }
    public static final RowKey of(final String str) {
        return getRowKeyBuilder().value(BytesUtil.toBytes(str),
                                        DefensiveCopyStrategy.NEVER).build();
    }
    public static final RowKey of(final String str, final Charset charset) {
        return
            getRowKeyBuilder()
                .value(BytesUtil.toBytes(str, charset), DefensiveCopyStrategy.NEVER)
                .build();
    }
    
    private String strRep;
    
    @Override
    public boolean equals(final Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        return ((otherObj instanceof RowKey) && (super.equals(otherObj)));
    }
    @Override
    public String toString() {
        if (this.strRep == null) {
            this.strRep =
                buildStrRep(ENTITY_PREFIX_FOR_TOSTRING, (strGen) -> {
                    strGen.append(BytesUtil.toString(getValue(DefensiveCopyStrategy.NEVER)));
                });
        }
        return this.strRep;
    }
    
    
    private RowKey(final Builder build) throws IllegalArgumentException {
        super(build);
    }
}
