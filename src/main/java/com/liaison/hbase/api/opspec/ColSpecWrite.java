/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.StringRepFormat;
import com.liaison.hbase.util.Util;

public class ColSpecWrite<P extends OperationSpec<P>> extends ColSpec<ColSpecWrite<P>, P> implements ColSpecWriteFluid<ColSpecWrite<P>>, ColSpecWriteFrozen, Serializable {

    private static final long serialVersionUID = -194106227851821468L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private Long ts;
    private Value value;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public ColSpecWrite<P> ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.ts = Util.validateExactlyOnceParam(Long.valueOf(ts), this, "ts", Long.class, this.ts);
        return self();
    }
    @Override
    public ColSpecWrite<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public Long getTS() {
        return this.ts;
    }
    @Override
    public Value getValue() {
        return this.value;
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    protected ColSpecWrite<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getFamily(), this, "fam", FamilyModel.class);
        Util.validateRequired(getColumn(), this, "qual", QualModel.class);
        Util.validateRequired(getValue(), this, "value", Value.class);
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[to-column]";
    }
    @Override
    protected void prepareStrRepAdditional(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.value != null) {
                Util.appendIndented(strGen, getDepth() + 1, "value: ", this.value, "\n");
            }
            if (this.ts != null) {
                Util.appendIndented(strGen, getDepth() + 1, "ts: ", this.ts, "\n");
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (this.value != null) {
                Util.append(strGen, "value=", this.value);
                if (this.ts != null) {
                    strGen.append(",");
                }
            }
            if (this.ts != null) {
                Util.append(strGen, "ts=", this.ts);
            }
            strGen.append("}");
        }
    }

    @Override
    protected int deepHashCode() {
        return (Util.hashCode(this.ts) ^ Util.hashCode(this.value));
    }

    @Override
    protected boolean deepEquals(final ColSpec<?, ?> otherColSpec) {
        final ColSpecWrite<?> otherCSW;
        if (otherColSpec instanceof ColSpecWrite) {
            otherCSW = (ColSpecWrite<?>) otherColSpec;
            return (Util.refEquals(this.ts, otherCSW.ts)
                    && Util.refEquals(this.value, otherCSW.value));
        }
        return false;
    }

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ColSpecWrite(final P parent) {
        super(parent);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
