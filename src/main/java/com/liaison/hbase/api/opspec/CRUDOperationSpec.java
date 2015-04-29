package com.liaison.hbase.api.opspec;

import java.util.HashSet;
import java.util.Set;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseQueryInputValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public abstract class CRUDOperationSpec<O extends CRUDOperationSpec<O>> extends OperationSpec<O> implements HBaseOperation<O> {
    
    private final HBaseContext context;
    private TableModel table;
    private RowKey rowKey;
    private Set<FamilyModel> families;
    private Set<QualModel> columns;
    private final LongValueSpec<O> tsSpec;
    
    protected abstract void validateInputs() throws HBaseQueryInputValidationException;
    protected abstract OpResult executeOperation() throws HBaseException;
    
    protected OpResult refineOutputs(OpResult initialResult) throws HBaseException {
        return initialResult;
    }
    
    public final OpResult exec() throws HBaseException {
        validateInputs();
        return refineOutputs(executeOperation());
    }
    
    public O row(final RowKey rowKey) {
        this.rowKey = rowKey;
        return self();
    }
    public O tbl(final TableModel table) {
        this.table = table;
        return self();
    }
    public O fam(final FamilyModel family) {
        return self();
    }
    public O col(final FamilyModel family, final QualModel qual) {
        return self();
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
    public Set<FamilyModel> fam() {
        return this.families;
    }
    public Set<QualModel> col() {
        return this.columns;
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
        this.families = new HashSet<>();
        this.columns = new HashSet<>();
    }
}
