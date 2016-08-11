/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.impl;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.api.request.fluid.fluent.CondSpecFluent;
import com.liaison.shachi.api.request.frozen.CondSpecFrozen;
import com.liaison.shachi.dto.Empty;
import com.liaison.shachi.dto.NullableValue;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.SpecValidationException;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.util.SpecUtil;
import com.liaison.shachi.util.StringRepFormat;

import java.io.Serializable;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P>
 */
public final class CondSpec<P extends OperationSpec<P>> extends ColSpec<CondSpec<P>, P> implements CondSpecFluent<CondSpec<P>, P>, CondSpecFrozen, Serializable {

    private static final long serialVersionUID = 328263884139551395L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private RowKey rowKey;
    private NullableValue value;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public CondSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.rowKey = Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    
    @Override
    public CondSpec<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }
    
    @Override
    public CondSpec<P> empty() throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        Util.validateExactlyOnce("value", Empty.class, this.value);
        this.value = Empty.getInstance();
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    public RowKey getRowKey() {
        return this.rowKey;
    }
    public NullableValue getValue() {
        return this.value;
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[given-condition]";
    }
    @Override
    protected void prepareStrRepAdditional(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.rowKey != null) {
                Util.appendIndented(strGen, getDepth() + 1, "rowKey: ", this.rowKey, "\n");
            }
            if (this.value != null) {
                Util.appendIndented(strGen, getDepth() + 1, "value: ", this.value, "\n");
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (this.rowKey != null) {
                Util.append(strGen, "rowKey=", this.rowKey);
                if (this.value != null) {
                    strGen.append(",");
                }
            }
            if (this.value != null) {
                Util.append(strGen, "value=", this.value);
            }
            strGen.append("}");
        }
    }

    @Override
    protected int deepHashCode() {
        return (Util.hashCode(this.rowKey) ^ Util.hashCode(this.value));
    }

    @Override
    protected boolean deepEquals(final ColSpec<?, ?> otherColSpec) {
        final CondSpec<?> otherCondSpec;
        if (otherColSpec instanceof CondSpec) {
            otherCondSpec = (CondSpec<?>) otherColSpec;
            return (Util.refEquals(this.rowKey, otherCondSpec.rowKey)
                    && Util.refEquals(this.value, otherCondSpec.value));
        }
        return false;
    }

    @Override
    protected CondSpec<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        SpecUtil.validateRequired(getRowKey(), this, "row", RowKey.class);
        SpecUtil.validateRequired(getFamily(), this, "fam", FamilyHB.class);
        SpecUtil.validateRequired(getColumn(), this, "column", QualHB.class);
        SpecUtil.validateRequired(getValue(), this, "value/empty", NullableValue.class);
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public CondSpec(final P parent) {
        super(parent);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
