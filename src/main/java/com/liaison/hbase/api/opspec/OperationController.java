package com.liaison.hbase.api.opspec;

import java.util.LinkedHashMap;

import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public class OperationController {
    
    private static enum State {
        ACCEPTING, EXECUTING;
    }
    
    private final Object stateLock;
    private final State state;
    private final HBaseControl control;
    private final HBaseContext context;
    private final LinkedHashMap<Object, OperationSpec<?>> ops;
    
    private final void verifyStateForAddingOps() throws IllegalStateException {
        Util.verifyState(State.ACCEPTING, this.state, this.stateLock);
    }
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
    
    public ReadOpSpec read(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final ReadOpSpec nextReadOp;
        verifyStateForAddingOps();
        nextReadOp = new ReadOpSpec(this.context, this);
        putOpWithNewHandle(handle, nextReadOp);
        return nextReadOp;
    }
    public CreateOpSpec create(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final CreateOpSpec nextCreateOp;
        verifyStateForAddingOps();
        nextCreateOp = new CreateOpSpec(this.context, this);
        putOpWithNewHandle(handle, nextCreateOp);
        return nextCreateOp;
    }
    public UpdateOpSpec update(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final UpdateOpSpec nextUpdateOp;
        verifyStateForAddingOps();
        nextUpdateOp = new UpdateOpSpec(this.context, this);
        putOpWithNewHandle(handle, nextUpdateOp);
        return nextUpdateOp;
    }
    public DeleteOpSpec delete(final Object handle) throws IllegalStateException, IllegalArgumentException {
        final DeleteOpSpec nextDeleteOp;
        verifyStateForAddingOps();
        nextDeleteOp = new DeleteOpSpec(this.context, this);
        putOpWithNewHandle(handle, nextDeleteOp);
        return nextDeleteOp;
    }
    
    public OpResult exec() {
        return null;
    }
    
    public OperationController(final HBaseControl control, final HBaseContext context) {
        Util.ensureNotNull(control, this, "control", HBaseControl.class);
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.stateLock = new Object();
        this.state = State.ACCEPTING;
        this.control = control;
        this.context = context;
        this.ops = new LinkedHashMap<>();
    }
}
