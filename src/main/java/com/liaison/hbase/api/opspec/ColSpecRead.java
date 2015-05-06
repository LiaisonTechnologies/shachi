package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.util.Util;

public final class ColSpecRead<P extends OperationSpec<P>> extends ColSpec<ColSpecRead<P>, P> implements Serializable {

    private static final long serialVersionUID = -3480030817298140795L;

    @Override
    protected ColSpecRead<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getFamily(), this, "fam", FamilyModel.class);
    }

    @Override
    protected String prepareStrRepHeadline() {
        return "[from-column]";
    }
    
    public ColSpecRead(final P parent) {
        super(parent);
    }
}
