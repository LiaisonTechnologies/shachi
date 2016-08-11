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
import com.liaison.shachi.api.request.fluid.fluent.ColSpecReadFluent;
import com.liaison.shachi.api.request.frozen.ColSpecReadFrozen;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.exception.SpecValidationException;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.VersioningModel;
import com.liaison.shachi.util.SpecUtil;

import java.io.Serializable;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P>
 */
public final class ColSpecRead<P extends OperationSpec<P>> extends ColSpec<ColSpecRead<P>, P> implements ColSpecReadFluent<ColSpecRead<P>, P>, ColSpecReadFrozen, Serializable {

    private static final long serialVersionUID = -3480030817298140795L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private LongValueSpec<ColSpecRead<P>> version;
    private boolean optional;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public LongValueSpec<ColSpecRead<P>> version() throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.version);
        /*
         * For version numbers, there should be a hard lower limit of zero, to accommodate
         * versioning schemes (like VersioningModel.QUALIFIER_LATEST) which subtract the number
         * from the maximum value in order to invert the sort order within HBase. The maximum value
         * is set to null here so that it will default to Long.MAX_VALUE.
         */
        this.version = VersioningModel.buildLongValueSpecForQualVersioning(this);
        return this.version;
    }

    @Override
    public ColSpecRead<P> version(final long version) throws IllegalStateException, IllegalArgumentException {
        version().eq(version);
        return self();
    }
    
    @Override
    public ColSpecRead<P> optional() throws IllegalStateException {
        /*
         * TODO
         *     The enforcement mechanism for non-optional elements (i.e. required) is really not
         *     working right now; fix it
         */
        prepMutation();
        this.optional = true;
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public LongValueSpec<ColSpecRead<P>> getVersion() {
        return this.version;
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * 
     * @param description
     * @return
     * @throws IllegalStateException
     */
    public FamilyQualifierPair toFQP(final String description) throws IllegalStateException {
        final String logMsg;
        final FamilyQualifierPair.Builder fqpBuild;
        fqpBuild =
            FamilyQualifierPair
                .getBuilder()
                .family(getFamily())
                .column(getColumn())
                .optional(this.optional);
        if (description != null) {
            fqpBuild.description(description);
        }
        try {
            return fqpBuild.build();
        } catch (IllegalArgumentException iaExc) {
            logMsg = FamilyQualifierPair.class.getSimpleName()
                     + " requires that both family and qualifier be specified; this "
                     + getClass().getSimpleName()
                     + " may be missing one or both: "
                     + toString();
            throw new IllegalStateException(logMsg, iaExc);
        }
    }
    
    /**
     * 
     * @return
     */
    public FamilyQualifierPair toFQP() {
        return toFQP(null);
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
    
    @Override
    protected ColSpecRead<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        SpecUtil.validateRequired(getFamily(), this, "fam", FamilyHB.class);
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ColSpecRead(final P parent, final Object handle) {
        super(parent, handle);
        this.optional = false;
    }
    public ColSpecRead(final P parent) {
        this(parent, null);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
