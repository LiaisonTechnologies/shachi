/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.Util;

public abstract class OperationSpec<O extends OperationSpec<O>> extends StatefulSpec<O, OperationController> implements Serializable {
    
    private static final long serialVersionUID = 5533663131351737507L;
    
    private final Object handle;
    private final HBaseContext context;
    
    public final Object getHandle() {
        return this.handle;
    }
    protected HBaseContext getContext() {
        return this.context;
    }
    
    public final OperationController then() throws SpecValidationException {
        freezeRecursive();
        return getParent();
    }
    
    @Override
    public final int prepareHashCode() {
        return this.handle.hashCode();
    }
    protected abstract boolean deepEquals(final OperationSpec<?> otherOpSpec);
    @Override
    public final boolean equals(final Object otherObj) {
        final OperationSpec<?> otherOpSpec;
        if (otherObj instanceof OperationSpec) {
            otherOpSpec = (OperationSpec<?>) otherObj;
            return ((Util.refEquals(this.handle, otherOpSpec.handle))
                    &&
                    deepEquals(otherOpSpec));
        }
        return false;
    }
    
    public OperationSpec(final Object handle, final HBaseContext context, final OperationController parent) {
        super(parent);
        Util.ensureNotNull(handle, this, "handle", Object.class);
        this.handle = handle;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
    }
}
