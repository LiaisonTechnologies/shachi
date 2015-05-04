package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.Util;

public abstract class ColSpec<C extends ColSpec<C, P>, P extends OperationSpec<P, ?>> extends CriteriaSpec<C, P> implements Serializable {
    
    private static final long serialVersionUID = 1772684254524544307L;
    
    private FamilyModel family;
    private QualModel column;
    
    public FamilyModel getFamily() {
        return this.family;
    }
    public QualModel getColumn() {
        return this.column;
    }
    
    public C fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.family =
            Util.validateExactlyOnceParam(family, this, "family", FamilyModel.class, this.family);
        return self();
    }
    public C qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.column =
            Util.validateExactlyOnceParam(column, this, "column", QualModel.class, this.column);
        return self();
    }
    
    protected abstract String prepareStrRepHeadline();
    protected void prepareStrRepAdditional(final StringBuilder strGen) {
        // do nothing
    }
    @Override
    public final void prepareStrRep(final StringBuilder strGen) {
        strGen.append(prepareStrRepHeadline());
        strGen.append("\n");
        if (this.family != null) {
            strGen.append("    family: ");
            strGen.append(this.family);
            strGen.append("\n");
        }
        if (this.column != null) {
            strGen.append("    qual: ");
            strGen.append(this.column);
            strGen.append("\n");
        }
        prepareStrRepAdditional(strGen);
    }
    
    public ColSpec(final P parent) {
        super(parent);
    }
}
