package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public final class RowSpec<P extends OperationSpec<P>> extends CriteriaSpec<RowSpec<P>, P> {
    
    private TableModel table;
    private RowKey rowKey;
    
    @Override
    public RowSpec<P> self() { return this; }
    
    public RowSpec<P> tbl(final TableModel table) throws IllegalStateException, IllegalArgumentException {
        this.table =
            Util.validateExactlyOnceParam(table, this, "table", TableModel.class, this.table);
        return self();
    }
    public RowSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        this.rowKey =
            Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    
    public RowSpec(final P parent) {
        super(parent);
    }
}
