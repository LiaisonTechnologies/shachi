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
import java.nio.charset.Charset;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class Value extends NullableValue implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;
    
    // ||========================================================================================||
    // ||    BUILDER (STATIC NESTED CLASS)                                                       ||
    // ||----------------------------------------------------------------------------------------||
    
    public static class Builder extends AbstractValueBuilder<Value, Builder> {
        @Override
        protected Builder self() {
            return this;
        }
        @Override
        public Value build() {
            return new Value(self());
        }
        public Empty voidBuild() {
            return Empty.getInstance();
        }
        
        private Builder() throws IllegalArgumentException {
            super();
        }
    }
    
    // ||----(builder)---------------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    STATIC METHODS                                                                      ||
    // ||----------------------------------------------------------------------------------------||
    
    public static Builder getValueBuilder() {
        return new Builder();
    }
    
    public static Value of(final byte[] value, final DefensiveCopyStrategy copyStrategy) {
        return getValueBuilder().value(value, copyStrategy).build();
    }
    @Deprecated
    public static Value of(final byte[] value) {
        return getValueBuilder().value(value).build();
    }
    public static Value of(final String str) {
        return getValueBuilder().value(BytesUtil.toBytes(str), DefensiveCopyStrategy.NEVER).build();
    }
    public static Value of(final String str, final Charset charset) {
        return
            getValueBuilder()
            .value(BytesUtil.toBytes(str, charset), DefensiveCopyStrategy.NEVER)
            .build();
    }
    
    // ||----(static methods)--------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    protected Value(final AbstractValueBuilder<?,?> build) throws IllegalArgumentException {
        super(build);
        Util.ensureNotNull(build.value, this, "value", byte[].class);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
