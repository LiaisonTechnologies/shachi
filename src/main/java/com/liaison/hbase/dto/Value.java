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
import com.liaison.hbase.util.Util;

public class Value extends NullableValue implements Serializable {
    
    private static final long serialVersionUID = 8100342808865479731L;
    
    public static Value of(final byte[] value, final DefensiveCopyStrategy copyStrategy) {
        return getValueBuilder().value(value, copyStrategy).build();
    }
    @Deprecated
    public static Value of(final byte[] value) {
        return getValueBuilder().value(value).build();
    }

    // TODO equals, toString, hashCode, etc.
    
    protected Value(final AbstractValueBuilder<?,?> build) throws IllegalArgumentException {
        super(build);
        Util.ensureNotNull(build.value, this, "value", byte[].class);
    }
}
