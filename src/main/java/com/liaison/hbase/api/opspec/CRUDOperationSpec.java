package com.liaison.hbase.api.opspec;

import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.client.Operation;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseOpInputValidationException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public abstract class CRUDOperationSpec<H extends Operation, O extends CRUDOperationSpec<H, O>> extends OperationSpec<O> implements HBaseOperation<O> {
    
    private final HBaseContext context;
    private TableModel table;
    private RowKey rowKey;
    private final List<ColumnSpec<O>> columns;
    private final LongValueSpec<O> tsSpec;
    
    public abstract H buildHBaseOp() throws HBaseOpInputValidationException;
    
    public O row(final RowKey rowKey) {
        this.rowKey = rowKey;
        return self();
    }
    public O tbl(final TableModel table) {
        this.table = table;
        return self();
    }
    public ColumnSpec<O> with() {
        final ColumnSpec<O> colSpec;
        colSpec = new ColumnSpec<O>(self());
        this.columns.add(colSpec);
        return colSpec;
    }
    
    protected HBaseContext context() {
        return this.context;
    }
    public TableModel tbl() {
        return this.table;
    }
    public RowKey row() {
        return this.rowKey;
    }
    
    public LongValueSpec<O> ts() {
        return this.tsSpec;
    }
    
    public CRUDOperationSpec(final HBaseContext context, final OperationController parent) {
        super(parent);
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.tsSpec = new LongValueSpec<O>(self());
        this.context = context;
        this.table = null;
        this.rowKey = null;
        this.columns = new LinkedList<>();
    }
}
