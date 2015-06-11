/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.commons.Util;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.request.OperationExecutor;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.exception.HBaseUnsupportedOperationException;
import com.liaison.hbase.util.TreeNodeRoot;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class OperationControllerDefault extends TreeNodeRoot<OperationControllerDefault> implements OperationController<OpResultSet>, Serializable {
    
    private static final long serialVersionUID = -6620685078075615195L;

    private static enum State {
        ACCEPTING, EXECUTING;
    }
    
    private final Object stateLock;
    private final State state;
    private final HBaseControl.HBaseDelegate delegate;
    private final HBaseContext context;
    private final LinkedHashMap<Object, OperationSpec<?>> ops;

    @Override
    protected OperationControllerDefault self() {
        return this;
    }
    
    /**
     * TODO
     * @throws IllegalStateException
     */
    private final void verifyStateForAddingOps() throws IllegalStateException {
        Util.verifyState(State.ACCEPTING, this.state, this.stateLock);
    }
    
    /**
     * TODO
     * @param handle
     * @param op
     * @throws IllegalArgumentException
     */
    private final void putOpWithNewHandle(final Object handle, final OperationSpec<?> op) throws IllegalArgumentException {
        if (handle == null) {
            throw new IllegalArgumentException("Operation must be specified with non-null handle");
        }
        if (ops.putIfAbsent(handle, op) != null) {
            throw new IllegalArgumentException("Handle "
                                               + handle
                                               + "already in use for other operation");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOpSpecDefault read(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final ReadOpSpecDefault nextReadOp;
        verifyStateForAddingOps();
        nextReadOp = new ReadOpSpecDefault(handle, this.context, this);
        putOpWithNewHandle(handle, nextReadOp);
        return nextReadOp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WriteOpSpecDefault write(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final WriteOpSpecDefault nextCreateOp;
        verifyStateForAddingOps();
        nextCreateOp = new WriteOpSpecDefault(handle, this.context, this);
        putOpWithNewHandle(handle, nextCreateOp);
        return nextCreateOp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpResultSet exec() throws HBaseUnsupportedOperationException, HBaseTableRowException, HBaseException {
        String logMsg;
        final OpResultSet opResSet;
        OperationSpec<?> opSpec;
        ReadOpSpecDefault readOpSpec;
        WriteOpSpecDefault writeOpSpec;
        
        opResSet = new OpResultSet();
        for (Map.Entry<Object, OperationSpec<?>> op : this.ops.entrySet()) {
            opSpec = op.getValue();
            if (opSpec instanceof ReadOpSpecDefault) {
                readOpSpec = (ReadOpSpecDefault) opSpec;
                opResSet.assimilate(readOpSpec, this.delegate.exec(readOpSpec));
            } else if (opSpec instanceof WriteOpSpecDefault) {
                writeOpSpec = (WriteOpSpecDefault) opSpec;
                opResSet.assimilate(writeOpSpec, this.delegate.exec(writeOpSpec));
            } else {
                if (opSpec == null) {
                    logMsg = "Null operation in chain";
                } else {
                    logMsg = "Unrecognized operation type "
                             + opSpec.getClass()
                             + ": "
                             + opSpec;
                }
                throw new HBaseUnsupportedOperationException(logMsg);
            }
        }
        return opResSet;
    }
    
    @Override
    public OperationExecutor<ListenableFuture<OpResultSet>> async() {
        return new OperationExecutorAsync(this.delegate, this);
    }
    
    /**
     * TODO
     * @param delegate
     * @param context
     */
    public OperationControllerDefault(final HBaseControl.HBaseDelegate delegate, final HBaseContext context) {
        Util.ensureNotNull(delegate, this, "delegate", HBaseControl.HBaseDelegate.class);
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.stateLock = new Object();
        this.state = State.ACCEPTING;
        this.delegate = delegate;
        this.context = context;
        this.ops = new LinkedHashMap<>();
    }
}
