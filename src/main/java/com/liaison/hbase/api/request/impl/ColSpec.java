/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.fluid.ColSpecFluid;
import com.liaison.hbase.api.request.frozen.ColSpecFrozen;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;

public abstract class ColSpec<C extends ColSpec<C, P>, P extends OperationSpec<P>> extends CriteriaSpec<C, P> implements ColSpecFluid<C>, ColSpecFrozen, Serializable {
    
    private static final long serialVersionUID = 1772684254524544307L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private FamilyModel family;
    private QualModel column;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public C fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.family =
            Util.validateExactlyOnceParam(family, this, "family", FamilyModel.class, this.family);
        return self();
    }
    @Override
    public C qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.column =
            Util.validateExactlyOnceParam(qual, this, "column", QualModel.class, this.column);
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public FamilyModel getFamily() {
        return this.family;
    }
    @Override
    public QualModel getColumn() {
        return this.column;
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    protected void prepareStrRepAdditional(final StringBuilder strGen, final StringRepFormat format) {
        // provide a default implementation which does nothing
    }
    @Override
    public final void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.family != null) {
                Util.appendIndented(strGen, getDepth() + 1, "family: ", this.family, "\n");
            }
            if (this.column != null) {
                Util.appendIndented(strGen, getDepth() + 1, "qual: ", this.column, "\n");
            }
            prepareStrRepAdditional(strGen, format);
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (this.family != null) {
                Util.append(strGen, "family=", this.family);
                if (column != null) {
                    strGen.append(",");
                }
            }
            if (this.column != null) {
                Util.append(strGen, "qual=", this.column);
            }
            prepareStrRepAdditional(strGen, format);
            strGen.append("}");
        }
    }
    
    protected abstract int deepHashCode();
    @Override
    public final int prepareHashCode() {
        return (Util.hashCode(this.family)
                ^ Util.hashCode(this.column)
                ^ deepHashCode());
    }
    protected abstract boolean deepEquals(final ColSpec<?,?> otherColSpec);
    @Override
    public final boolean equals(final Object otherObj) {
        final ColSpec<?,?> otherColSpec;
        if (otherObj instanceof ColSpec) {
            otherColSpec = (ColSpec<?,?>) otherObj;
            return (Util.refEquals(this.family, this.family)
                    && Util.refEquals(this.column, this.column)
                    && deepEquals(otherColSpec));
        }
        return false;
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ColSpec(final P parent) {
        super(parent);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}