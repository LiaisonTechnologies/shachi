package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public final class RowSpec<P extends OperationSpec<P>> extends CriteriaSpec<RowSpec<P>, P> implements Serializable {
    
    private static final long serialVersionUID = 1106826447086026044L;
    
    private TableModel table;
    private RowKey rowKey;
    
    @Override
    public RowSpec<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getTable(), this, "tbl", TableModel.class);
        Util.validateRequired(getRowKey(), this, "row", RowKey.class);
    }
    
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
    protected String prepareStrRepHeadline() {
        return "[@table,@row]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        if (this.table != null) {
            Util.appendIndented(strGen, getDepth() + 1, "table: ", this.table, "\n");
        }
        if (this.rowKey != null) {
            Util.appendIndented(strGen, getDepth() + 1, "rowKey: ", this.rowKey, "\n");
        }
    }
    
    public RowSpec(final P parent) {
        super(parent);
    }

    @Override
    protected int prepareHashCode() {
        return (Util.hashCode(this.table) ^ Util.hashCode(this.rowKey));
    }

    @Override
    public boolean equals(final Object otherObj) {
        final RowSpec<?> otherRowSpec;
        if (otherObj instanceof RowSpec) {
            otherRowSpec = (RowSpec<?>) otherObj;
            return (Util.refEquals(this.table, otherRowSpec.table)
                    && Util.refEquals(this.rowKey, otherRowSpec.rowKey));
        }
        return false;
    }
}
