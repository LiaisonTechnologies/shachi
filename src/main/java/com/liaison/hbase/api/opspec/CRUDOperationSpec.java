package com.liaison.hbase.api.opspec;

import java.util.HashSet;
import java.util.Set;

import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.api.RowKey;
import com.liaison.hbase.cnxn.HBaseContext;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseQueryInputValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.AbstractSelfRef;
import com.liaison.hbase.util.Util;

public abstract class CRUDOperationSpec<O extends CRUDOperationSpec<O>> extends AbstractSelfRef<O> {
    
    private final HBaseContext context;
    private TableModel table;
    private RowKey rowKey;
    private Set<FamilyModel> families;
    private Set<QualModel> columns;
    private LongValueSpec<O> tsSpec;
    
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
    public O col(final QualModel qual) {
        return self();
    }
    
    public LongValueSpec<O> ts() {
        this.tsSpec = new LongValueSpec<O>(self());
        return this.tsSpec;
    }
    
    public CRUDOperationSpec(final HBaseContext context) throws IllegalStateException {
        super();
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        this.tsSpec = null;
        this.table = null;
        this.rowKey = null;
        this.families = new HashSet<>();
        this.columns = new HashSet<>();
    }
}
