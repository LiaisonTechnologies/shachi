package com.liaison.hbase.api.opspec;

import java.util.LinkedList;
import java.util.List;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public class OperationController {
    private final HBaseContext context;
    private List<OperationSpec<?>> ops;
    
    public ReadOpSpec read() {
        final ReadOpSpec nextReadOp;
        nextReadOp = new ReadOpSpec(this.context, this);
        ops.add(nextReadOp);
        return nextReadOp;
    }
    public CreateOpSpec create() {
        final CreateOpSpec nextCreateOp;
        nextCreateOp = new CreateOpSpec(this.context, this);
        ops.add(nextCreateOp);
        return nextCreateOp;
    }
    public UpdateOpSpec update() {
        final UpdateOpSpec nextUpdateOp;
        nextUpdateOp = new UpdateOpSpec(this.context, this);
        ops.add(nextUpdateOp);
        return nextUpdateOp;
    }
    public DeleteOpSpec delete() {
        final DeleteOpSpec nextDeleteOp;
        nextDeleteOp = new DeleteOpSpec(this.context, this);
        ops.add(nextDeleteOp);
        return nextDeleteOp;
    }
    
    public OperationController(final HBaseContext context) {
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        this.ops = new LinkedList<>();
    }
}
