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
import com.liaison.hbase.api.request.fluid.fluent.ColSpecReadFluent;
import com.liaison.hbase.api.request.frozen.ColSpecReadFrozen;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.util.SpecUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

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

    /**
     * Set of FamilyQualifierPair entries generated from this spec when it is translated to a Get
     * object for HBase execution. Indicates the correspondence between this source specification
     * and the family/qualifier combinations which key the result set, and used in order to
     * re-associate the spec with the result in the OpResultSet.
     *
     * Note that this field is not populated until the parent ReadOpSpec transitions from FLUID
     * state to FROZEN.
     */
    private Set<FamilyQualifierPair> resultColumnAssoc;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public LongValueSpec<ColSpecRead<P>> version() throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.version);
        this.version = new LongValueSpec<>(this);
        return this.version;
    }

    @Override
    public ColSpecRead<P> version(final long version) throws IllegalStateException, IllegalArgumentException {
        version().eq(version);
        return self();
    }
    
    @Override
    public ColSpecRead<P> optional() throws IllegalStateException {
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

    @Override
    public void setResultColumnAssoc(final Set<FamilyQualifierPair> resultColumnAssoc) {
        prepPostFreezeOp("setResultColumnAssoc");
        Util.validateExactlyOnceParam(resultColumnAssoc,
                                      this,
                                      "resultColumnAssoc",
                                      Set.class,
                                      this.resultColumnAssoc);
        this.resultColumnAssoc = Collections.unmodifiableSet(resultColumnAssoc);
    }

    @Override
    public Set<FamilyQualifierPair> getResultColumnAssoc() {
        prepPostFreezeOp("getResultColumnAssoc");
        return this.resultColumnAssoc;
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
                .qual(getColumn())
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
        SpecUtil.validateRequired(getFamily(), this, "fam", FamilyModel.class);
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ColSpecRead(final P parent) {
        super(parent);
        this.optional = false;
        this.resultColumnAssoc = null;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
