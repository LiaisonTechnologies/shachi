package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public final class RowSpec<P extends OperationSpec<P, ?>> extends CriteriaSpec<RowSpec<P>, P> implements Serializable {
    
    private static final long serialVersionUID = 1106826447086026044L;
    
    private TableModel table;
    private RowKey rowKey;
    
    @Override
    public RowSpec<P> self() { return this; }
    
    public TableModel getTable() {
        return this.table;
    }
    public RowKey getRowKey() {
        return this.rowKey;
    }
    
    public RowSpec<P> tbl(final TableModel table) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.table =
            Util.validateExactlyOnceParam(table, this, "table", TableModel.class, this.table);
        return self();
    }
    public RowSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.rowKey =
            Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        
    }
    
    public RowSpec(final P parent) {
        super(parent);
    }
}
