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
import com.liaison.shachi.api.request.fluid.ColSpecFluid;
import com.liaison.shachi.api.request.frozen.ColSpecFrozen;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.util.StringRepFormat;

import java.io.Serializable;

public abstract class ColSpec<C extends ColSpec<C, P>, P extends OperationSpec<P>> extends CriteriaSpec<C, P> implements ColSpecFluid<C>, ColSpecFrozen, Serializable {
    
    private static final long serialVersionUID = 1772684254524544307L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private Object handle;
    private FamilyHB family;
    private QualHB column;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public C handle(final Object handle) throws IllegalStateException {
        prepMutation();
        this.handle = handle;
        return self();
    }

    @Override
    public C fam(final FamilyHB family) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.family =
            Util.validateExactlyOnceParam(family, this, "family", FamilyHB.class, this.family);
        return self();
    }
    @Override
    public C qual(final QualHB qual) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.column =
            Util.validateExactlyOnceParam(qual, this, "column", QualHB.class, this.column);
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public Object getHandle() {
        return this.handle;
    }
    @Override
    public FamilyHB getFamily() {
        return this.family;
    }
    @Override
    public QualHB getColumn() {
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
                Util.appendIndented(strGen, getDepth() + 1, "column: ", this.column, "\n");
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
                Util.append(strGen, "column=", this.column);
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
            return (Util.refEquals(this.handle, otherColSpec.handle)
                    && Util.refEquals(this.family, otherColSpec.family)
                    && Util.refEquals(this.column, otherColSpec.column)
                    && deepEquals(otherColSpec));
        }
        return false;
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ColSpec(final P parent, final Object handle) {
        super(parent);
        this.handle = handle;
    }
    public ColSpec(final P parent) {
        this(parent, null);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
