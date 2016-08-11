package com.liaison.shachi.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.shachi.api.request.fluid.fluent.LongValueSpecFluent;
import com.liaison.shachi.api.request.frozen.LongValueSpecFrozen;
import com.liaison.shachi.util.TreeNode;

import java.io.Serializable;

public class LongValueSpecConfinedParent<S extends TreeNode<S>, P extends StatefulSpec<P, ?>> extends CriteriaSpec<LongValueSpecConfinedParent<S, P>, S> implements LongValueSpecFluent<S>, LongValueSpecFrozen, Serializable {

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private final LongValueSpec<?> core;

    // ||----(instance properties)---------------------------------------------------------------||

    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public LongValueSpecConfinedParent<S, P> gt(final long value) throws ArithmeticException {
        core.gt(value);
        return this;
    }
    @Override
    public LongValueSpecConfinedParent<S, P> ge(final long value) throws ArithmeticException {
        core.ge(value);
        return this;
    }
    @Override
    public LongValueSpecConfinedParent<S, P> eq(final long value) throws ArithmeticException {
        core.eq(value);
        return this;
    }
    @Override
    public LongValueSpecConfinedParent<S, P> lt(final long value) throws ArithmeticException {
        core.lt(value);
        return this;
    }
    @Override
    public LongValueSpecConfinedParent<S, P> le(final long value) throws ArithmeticException {
        core.le(value);
        return this;
    }

    // ||----(instance methods: API: fluid)------------------------------------------------------||

    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public long getTypeMin() {
        return core.getTypeMin();
    }
    @Override
    public long getTypeMax() {
        return core.getTypeMax();
    }
    @Override
    public Long getLowerBoundInclusive() {
        return core.getLowerBoundInclusive();
    }
    @Override
    public Long getUpperBoundExclusive() {
        return core.getUpperBoundExclusive();
    }
    @Override
    public boolean isLowerBounded() {
        return core.isLowerBounded();
    }
    @Override
    public boolean isUpperBounded() {
        return core.isUpperBounded();
    }
    @Override
    public boolean isBounded() {
        return core.isBounded();
    }
    @Override
    public Long singleValue() {
        return core.singleValue();
    }
    @Override
    public boolean isSingleValue() {
        return core.isSingleValue();
    }

    // ||----(instance methods: API: frozen)-----------------------------------------------------||

    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||


    @Override
    protected LongValueSpecConfinedParent<S, P> self() {
        return null;
    }
    @Override
    protected String prepareStrRepHeadline() {
        return core.prepareStrRepHeadline();
    }
    @Override
    protected int prepareHashCode() {
        return core.prepareHashCode();
    }
    @Override
    public boolean equals(final Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        return core.equals(otherObj);
    }
    @Override
    public S and() {
        return getParent();
    }

    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public LongValueSpecConfinedParent(final S confinedParent, final LongValueSpec<P> core) throws IllegalArgumentException, ArithmeticException {
        super(confinedParent);
        Util.ensureNotNull(core, this, "core", LongValueSpec.class);
        this.core = core;
    }

    // ||----(constructors)----------------------------------------------------------------------||
}
