/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;

import java.io.Serializable;

public abstract class OperationSpec<O extends OperationSpec<O>> extends StatefulSpec<O, OperationControllerDefault> implements Serializable {
    
    private static final long serialVersionUID = 5533663131351737507L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private final Object handle;
    private final HBaseContext context;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS                                                                    ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * TODO
     * @return
     */
    public final Object getHandle() {
        return this.handle;
    }
    
    /**
     * TODO
     * @return
     */
    protected HBaseContext getContext() {
        return this.context;
    }
    
    /**
     * TODO
     * @return
     * @throws SpecValidationException
     */
    public final OperationControllerDefault then() throws SpecValidationException {
        freezeRecursive();
        return getParent();
    }
    
    @Override
    public final int prepareHashCode() {
        return this.handle.hashCode();
    }
    
    /**
     * TODO
     * @param otherOpSpec
     * @return
     */
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
    
    // ||----(instance methods)------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public OperationSpec(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(parent);
        Util.ensureNotNull(handle, this, "handle", Object.class);
        this.handle = handle;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
