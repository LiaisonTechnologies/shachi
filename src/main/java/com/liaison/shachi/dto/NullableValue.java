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
import com.liaison.shachi.util.AbstractSelfRefBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Consumer;

public abstract class NullableValue implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;

    protected abstract static class AbstractValueBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        protected byte[] value;
        public B value(final byte[] value, final DefensiveCopyStrategy copyStrategy) {
            this.value = BytesUtil.setInternalByteArray(value, copyStrategy);
            return self();
        }
        @Deprecated
        public B value(final byte[] value) {
            return value(value, DefensiveCopyStrategy.DEFAULT);
        }
        
        protected AbstractValueBuilder() throws IllegalArgumentException {
            this.value = null;
        }
    }
    
    private static final String ENTITY_PREFIX_FOR_TOSTRING = "";
    
    protected static String buildStrRep(final String entityTypeIdentifier, final Consumer<StringBuilder> contentGenerator) {
        final StringBuilder strGen;
        strGen = new StringBuilder();
        strGen.append("<");
        if (entityTypeIdentifier != null) {
            strGen.append(entityTypeIdentifier);
        }
        contentGenerator.accept(strGen);
        strGen.append(">");
        return strGen.toString();
    }
    protected static String buildStrRep(final Consumer<StringBuilder> contentGenerator) {
        return buildStrRep(null, contentGenerator);
    }
    
    private final byte[] value;
    private Integer hc;
    private String strRep;
    
    public byte[] getValue(final DefensiveCopyStrategy copyStrategy) {
        return BytesUtil.getInternalByteArray(this.value, copyStrategy);
    }
    @Deprecated
    public byte[] getValue() {
        return getValue(DefensiveCopyStrategy.DEFAULT);
    }
    
    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(Arrays.hashCode(getValue(DefensiveCopyStrategy.NEVER)));
        }
        return this.hc.intValue();
    }
    @Override
    public boolean equals(final Object otherObj) {
        final NullableValue otherVal;
        if (this == otherObj) {
            return true;
        }
        if (otherObj instanceof NullableValue) {
            otherVal = (Value) otherObj;
            return BytesUtil.refEquals(this.value, otherVal.value);
        }
        return false;
    }
    @Override
    public String toString() {
        if (this.strRep == null) {
            this.strRep =
                buildStrRep(ENTITY_PREFIX_FOR_TOSTRING, (strGen) -> {
                    strGen.append(BytesUtil.toString(this.value));
                });
        }
        return this.strRep;
    }
    
    protected NullableValue(final AbstractValueBuilder<?,?> build) throws IllegalArgumentException {
        this.value = build.value;
    }
}
