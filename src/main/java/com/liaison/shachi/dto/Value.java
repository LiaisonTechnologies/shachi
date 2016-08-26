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
