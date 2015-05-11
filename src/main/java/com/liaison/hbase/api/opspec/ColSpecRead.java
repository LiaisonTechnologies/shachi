package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.util.Util;

public final class ColSpecRead<P extends OperationSpec<P>> extends ColSpec<ColSpecRead<P>, P> implements Serializable {

    private static final long serialVersionUID = -3480030817298140795L;

    private boolean optional;
    
    @Override
    protected ColSpecRead<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getFamily(), this, "fam", FamilyModel.class);
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    public ColSpecRead<P> optional() throws IllegalStateException {
        prepMutation();
        this.optional = true;
        return this;
    }

    @Override
    protected String prepareStrRepHeadline() {
        return "[from-column]";
    }

    @Override
    protected int deepHashCode() {
        return 0;
    }

    @Override
    protected boolean deepEquals(final ColSpec<?, ?> otherColSpec) {
        return (otherColSpec instanceof ColSpecRead);
    }
    
    public ColSpecRead(final P parent) {
        super(parent);
        this.optional = false;
    }
}
