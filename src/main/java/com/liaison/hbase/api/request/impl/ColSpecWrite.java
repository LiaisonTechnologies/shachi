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
import com.liaison.hbase.api.request.fluid.fluent.ColSpecWriteFluent;
import com.liaison.hbase.api.request.frozen.ColSpecWriteFrozen;
import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.NullableValue;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.SpecUtil;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;

public class ColSpecWrite<P extends OperationSpec<P>> extends ColSpec<ColSpecWrite<P>, P> implements ColSpecWriteFluent<ColSpecWrite<P>, P>, ColSpecWriteFrozen, Serializable {

    private static final long serialVersionUID = -194106227851821468L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private Long version;
    private Long ts;
    private NullableValue value;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public ColSpecWrite<P> version(final long version) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.version =
            Util.validateExactlyOnceParam(Long.valueOf(version),
                                          this,
                                          "version",
                                          Long.class,
                                          this.version);
        return self();
    }
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
    @Override
    public ColSpecWrite<P> empty(final Empty empty) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(empty, this, "empty", Empty.class, this.value);
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public Long getVersion() {
        return this.version;
    }
    @Override
    public Long getTS() {
        return this.ts;
    }
    @Override
    public NullableValue getValue() {
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
        SpecUtil.validateRequired(getFamily(), this, "fam", FamilyModel.class);
        SpecUtil.validateRequired(getColumn(), this, "qual", QualModel.class);
        SpecUtil.validateRequired(getValue(), this, "value", NullableValue.class);
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

    public ColSpecWrite(final P parent, final Object handle) {
        super(parent, handle);
        this.ts = null;
        this.version = null;
    }
    public ColSpecWrite(final P parent) {
        this(parent, null);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
