/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.commons.Util;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.request.OperationController;
import com.liaison.shachi.api.request.OperationExecutor;
import com.liaison.shachi.api.response.OpResultSet;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class OperationExecutorAsync implements OperationExecutor<ListenableFuture<OpResultSet>> {

    private final OperationController<OpResultSet> coreOperation;
    private final HBaseControl.HBaseDelegate delegate;

    /**
     * {@inheritDoc}
     * @see com.liaison.shachi.api.request.OperationController#exec()
     */
    @Override
    public ListenableFuture<OpResultSet> exec() {
        return this.delegate.execAsync(this.coreOperation::exec);
    }

    /**
     * 
     */
    public OperationExecutorAsync(final HBaseControl.HBaseDelegate delegate, final OperationController<OpResultSet> coreOperation) {
        Util.ensureNotNull(delegate, this, "delegate", HBaseControl.HBaseDelegate.class);
        this.delegate = delegate;
        Util.ensureNotNull(coreOperation, this, "coreOperation", OperationController.class);
        this.coreOperation = coreOperation;
    }

}
