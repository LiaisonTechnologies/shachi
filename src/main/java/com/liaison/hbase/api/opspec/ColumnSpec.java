package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.Util;

public class ColumnSpec<O extends OperationSpec<O>> {

    private final O parent;
    private FamilyModel family;
    private QualModel qual;
    private Value value;

    public ColumnSpec<O> fam(final FamilyModel family) {
        this.family = family;
        return this;
    }
    public ColumnSpec<O> col(final QualModel qual) {
        this.qual = qual;
        return this;
    }
    public ColumnSpec<O> val(final Value value) {
        this.value = value;
        return this;
    }
    
    public OperationSpec<?> and() {
        return this.parent;
    }
    
    public ColumnSpec(final O parent) {
        Util.ensureNotNull(parent, this, "parent", OperationSpec.class);
        this.parent = parent;
    }

}
