package com.liaison.hbase.api.opspec;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public abstract class TableRowOpSpec<O extends TableRowOpSpec<O>> extends OperationSpec<O> {
    
    private static final long serialVersionUID = -7192144630587757596L;
    
    private RowSpec<O> tableRow;
    
    public RowSpec<O> getTableRow() {
        return this.tableRow;
    }
    protected void setTableRow(final RowSpec<O> tableRow) throws IllegalArgumentException, IllegalStateException {
        prepMutation();
        Util.validateExactlyOnceParam(tableRow, this, "tableRow", RowSpec.class, this.tableRow);
        this.tableRow = tableRow;
    }

    public TableRowOpSpec(final Object handle, final HBaseContext context, final OperationController parent) {
        super(handle, context, parent);
        this.tableRow = null;
    }
}
