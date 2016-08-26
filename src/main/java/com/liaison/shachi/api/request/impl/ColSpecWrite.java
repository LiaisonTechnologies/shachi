/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.api.request.impl;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.api.request.fluid.fluent.ColSpecWriteFluent;
import com.liaison.shachi.api.request.frozen.ColSpecWriteFrozen;
import com.liaison.shachi.dto.Empty;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.dto.NullableValue;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.SpecValidationException;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.model.ser.CellSerializer;
import com.liaison.shachi.util.SpecUtil;
import com.liaison.shachi.util.StringRepFormat;

import java.io.Serializable;

public final class ColSpecWrite<P extends TableRowOpSpec<P>> extends ColSpec<ColSpecWrite<P>, P> implements ColSpecWriteFluent<ColSpecWrite<P>, P>, ColSpecWriteFrozen, Serializable {

    private static final long serialVersionUID = -194106227851821468L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private Long version;
    private Long ts;
    /**
     * "Prototype" of the value to be written; can store either a NullableValue which stores the
     * literal value to be written to HBase, or another kind of Object which must be serialized
     * prior to persistence. The type is ambiguous while the write spec is in a FLUID state; when
     * the spec moves to FROZEN state, the validate() method populates the NullableValue value
     * field with the literal bytes to be written, performing serialization using the Serializer if
     * needed.
     */
    private Object valueProto;
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
        this.valueProto =
            Util.validateExactlyOnceParam(value, this, "value", Value.class, this.valueProto);
        return self();
    }
    @Override
    public ColSpecWrite<P> empty(final Empty empty) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.valueProto =
            Util.validateExactlyOnceParam(empty, this, "empty", Empty.class, this.valueProto);
        return self();
    }
    @Override
    public ColSpecWrite<P> content(final Object dataObj) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.valueProto =
            Util.validateExactlyOnceParam(dataObj, this, "dataObj", Object.class, this.valueProto);
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
        prepPostFreezeOp("getValue");
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
        String logMsg;
        final RowSpec<?> rowSpec;
        final CellSerializer cellSer;

        super.validate();
        SpecUtil.validateRequired(getFamily(), this, "fam", FamilyHB.class);
        SpecUtil.validateRequired(getColumn(), this, "column", QualHB.class);
        SpecUtil.validateRequired(this.valueProto, this, "value", Object.class);

        if (this.valueProto instanceof NullableValue) {
            /*
             * In this case, the client has already prepared a value (or empty) to be assigned as
             * the value for this cell, so there is no need to serialize some other object.
             */
            this.value = (NullableValue) this.valueProto;
        } else {
            /*
             * In this case, the assigned value is of some non-prepared type, so serialization is
             * required in order to produce a NullableValue which the framework can write to the
             * cell.
             */
            rowSpec = getParent().getTableRow();
            cellSer =
                SpecUtil.identifySerializer(FamilyQualifierPair.of(getFamily(), getColumn()),
                                            getColumn(),
                                            getFamily(),
                                            ((rowSpec == null)?null:rowSpec.getTable()));
            if (cellSer != null) {
                this.value =
                    Value.of(cellSer.serialize(this.valueProto), DefensiveCopyStrategy.NEVER);
            } else {
                logMsg = "Value for column "
                         + toString()
                         + " is not of type "
                         + NullableValue.class.getSimpleName()
                         + " and therefore requires serialization, but no serializer is defined";
                throw new SpecValidationException(SpecState.FLUID, SpecState.FROZEN, this, logMsg);
            }
        }
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
