package com.liaison.hbase.api.opspec;

import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.Util;

public abstract class ColSpec<C extends ColSpec<C, P>, P extends OperationSpec<P>> extends CriteriaSpec<C, P> {
    
    private FamilyModel family;
    private QualModel column;
    
    public C fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException {
        this.family =
            Util.validateExactlyOnceParam(family, this, "family", FamilyModel.class, this.family);
        return self();
    }
    public C qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException {
        this.column =
            Util.validateExactlyOnceParam(column, this, "column", QualModel.class, this.column);
        return self();
    }
    
    public ColSpec(final P parent) {
        super(parent);
    }
}
