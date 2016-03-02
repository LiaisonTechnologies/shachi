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
import com.liaison.hbase.api.request.fluid.fluent.LongValueSpecFluent;
import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;
import java.util.function.BiPredicate;

public final class LongValueSpec<P extends StatefulSpec<P, ?>> extends CriteriaSpec<LongValueSpec<P>, P> implements LongValueSpecFluent<P>, LongValueSpecFrozen, Serializable {
    
    private static final long serialVersionUID = 7413385960948152177L;
    
    // ||========================================================================================||
    // ||    CONSTANTS                                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    public static final String SETNOT_LOWER_INC = "[";
    public static final String SETNOT_LOWER_EXC = "(";
    public static final String SETNOT_UPPER_INC = "]";
    public static final String SETNOT_UPPER_EXC = ")";
    
    private static final long TYPEMIN_DEFAULT = Long.MIN_VALUE;
    private static final long TYPEMAX_DEFAULT = Long.MAX_VALUE;
    
    // ||----(constants)-------------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    STATIC METHODS                                                                      ||
    // ||----------------------------------------------------------------------------------------||

    /**
     * Ensure that the lower and upper bounds meet the conditions of the given predicate function. 
     * Typically, said predicate will ensure either that the lower bound is lesser-than-or-equal to
     * the upper bound, or that it is lesser-than the upper bound.
     * @param compare
     * @param lower
     * @param upper
     * @param setNotationLower
     * @param setNotationUpper
     * @throws ArithmeticException
     */
    private static void validateBounds(final BiPredicate<Long, Long> compare, final Long lower, final Long upper, final String setNotationLower, final String setNotationUpper) throws ArithmeticException {
        if ((lower != null) && (upper != null)) {
            if (!compare.test(lower, upper)) {
                throw new ArithmeticException("Long-value range set has no elements: "
                                              + setNotationLower
                                              + lower
                                              + ","
                                              + upper
                                              + setNotationUpper);
            }
        }
    }
    /**
     * Ensure that the given lower bound is lesser-than the upper bound.
     * @param lower
     * @param upper
     * @throws ArithmeticException
     */
    private static void validateBoundsIncExc(final Long lower, final Long upper) throws ArithmeticException {
        /*
         * If the lower value is inclusive and the upper bound is exclusive, then the upper bound
         * is not part of the range, so the range is only valid (has at least one element) if the
         * lower bound is SMALLER THAN (NOT equal to) the upper bound.
         */
        validateBounds((low, high) -> low.longValue() < high.longValue(),
                       lower, upper, SETNOT_LOWER_INC, SETNOT_UPPER_EXC);
    }
    /**
     * Ensure that the given lower bound is lesser-than-or-equal to the upper bound.
     * @param lower
     * @param upper
     * @throws ArithmeticException
     */
    private static void validateBoundsIncInc(final Long lower, final Long upper) throws ArithmeticException {
        /*
         * If both bounds are inclusive, then the lower bound must be SMALLER THAN OR EQUAL TO the
         * upper bound.
         */
        validateBounds((low, high) -> low.longValue() <= high.longValue(),
                       lower, upper, SETNOT_LOWER_INC, SETNOT_UPPER_INC);
    }
    
    // ||----(static methods)--------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private final long typeMin;
    private final long typeMax;
    
    private Long lowerBoundInclusive;
    private Long upperBoundExclusive;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    /**
     * greater-than
     * @param value
     */
    @Override
    public LongValueSpec<P> gt(long value) throws ArithmeticException {
        prepMutation();
        /*
         * Translate the greater-than value to an inclusive minimum bound:
         *     (1) add 1 (since the value as provided should be EXcluded)
         *     (2) if the result is <= this.typeMax, then set it; otherwise throw an
         *         ArithmeticException to indicate that the operation is not possible (as a value
         *         cannot be greater than the max Long)
         */
        if (value >= this.typeMax) {
            throw new ArithmeticException("Overflow: cannot require a value greater than the "
                                          + "maximum for the spec type ("
                                          + this.typeMax
                                          + ")");
        } else {
            // set the inclusive lower bound to 1 plus the given (excluded) minimum
            setLowerBoundInclusive(Long.valueOf(value + 1));
        }
        return this;
    }
    
    /**
     * greater-than-or-equal-to
     * @param value
     */
    @Override
    public LongValueSpec<P> ge(long value) throws ArithmeticException {
        prepMutation();
        setLowerBoundInclusive(Long.valueOf(value));
        return this;
    }
    
    /**
     * equal-to
     * @param value
     */
    @Override
    public LongValueSpec<P> eq(long value) throws ArithmeticException {
        prepMutation();
        /*
         * equal-to equates to:
         *     less-than-or-equal-to
         *     OR
         *     greater-than-or-equal-to
         * so use those two methods to set the upper and lower bounds
         */
        le(value);
        ge(value);
        return this;
    }
    
    /**
     * less-than
     * @param value
     */
    @Override
    public LongValueSpec<P> lt(long value) throws ArithmeticException {
        prepMutation();
        setUpperBoundExclusive(Long.valueOf(value));
        return this;
    }
    
    /**
     * less-than-or-equal-to
     * @param value
     */
    @Override
    public LongValueSpec<P> le(long value) {
        prepMutation();
        /*
         * Translate the less-than-or-equal value to an exclusive maximum bound:
         *     (1) add 1 (the "-or-equal" means that the given value should be INcluded)
         *     (2) if the result is <= this.typeMax, then set it; otherwise set it null, since it
         *         equates to no maximum
         */
        if (value >= this.typeMax) {
            setUpperBoundExclusive(null);
        } else {
            // set the exclusive upper bound to 1 plus the given (inclusive) max
            setUpperBoundExclusive(Long.valueOf(value + 1));
        }
        return this;
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public long getTypeMin() {
        return this.typeMin;
    }
    @Override
    public long getTypeMax() {
        return this.typeMax;
    }

    @Override
    public Long getLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }
    @Override
    public Long getUpperBoundExclusive() {
        return this.upperBoundExclusive;
    }

    @Override
    public boolean isLowerBounded() {
        return (this.lowerBoundInclusive != null);
    }
    @Override
    public boolean isUpperBounded() {
        return (this.upperBoundExclusive != null);
    }
    @Override
    public boolean isBounded() {
        return (isLowerBounded() || isUpperBounded());
    }

    @Override
    public Long singleValue() {
        final Long lower;
        final Long upper;

        lower = this.lowerBoundInclusive;
        upper = this.upperBoundExclusive;

        if ((lower != null) && (upper != null)) {
            if ((lower.longValue() + 1) == (upper.longValue())) {
                return lower;
            }
        }
        return null;
    }
    @Override
    public boolean isSingleValue() {
        return (singleValue() != null);
    }

    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    private void setLowerBoundInclusive(Long lower) throws ArithmeticException {
        // an inclusive lower bound equal to the smallest possible long equates to the absence of a
        // lower bound, so set the lower bound to null instead 
        if ((lower != null) && (lower.longValue() <= this.typeMin)) {
            lower = null;
        }
        validateBoundsIncExc(lower, this.upperBoundExclusive);
        this.lowerBoundInclusive = lower;
    }
    
    private void setUpperBoundExclusive(final Long upper) throws ArithmeticException {
        if ((upper != null) && (upper.longValue() <= this.typeMin)) {
            throw new ArithmeticException("Long-int underflow; cannot require a value less than "
                                          + "the minimum long value ("
                                          + this.typeMin
                                          + ")");
        }
        validateBoundsIncExc(this.lowerBoundInclusive, upper);
        this.upperBoundExclusive = upper;
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[long-value]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.lowerBoundInclusive != null) {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "lower bound (inclusive): ",
                                    this.lowerBoundInclusive,
                                    "\n");
            } else {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "lower bound (inclusive): -INFINITY (type min: ",
                                    Long.valueOf(this.typeMin),
                                    ")\n");
            }
            if (this.upperBoundExclusive != null) {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "upper bound (exclusive): ",
                                    this.upperBoundExclusive,
                                    "\n");
            } else {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "upper bound (exclusive): +INFINITY (type max: ",
                                    Long.valueOf(this.typeMax),
                                    ")\n");
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("[");
            if (this.lowerBoundInclusive != null) {
                strGen.append(this.lowerBoundInclusive);
            } else {
                Util.append(strGen, "-INF:type-min=", Long.valueOf(this.typeMin), ")");
            }
            strGen.append(",");
            if (this.upperBoundExclusive != null) {
                strGen.append(this.upperBoundExclusive);
            } else {
                Util.append(strGen, "+INF:type-max=", Long.valueOf(this.typeMax), ")");
            }
            strGen.append(")");
        }
    }
    
    @Override
    protected int prepareHashCode() {
        return (Util.hashCode(this.lowerBoundInclusive)
                ^ Util.hashCode(this.upperBoundExclusive)
                ^ Long.hashCode(this.typeMin)
                ^ Long.hashCode(this.typeMax));
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        final LongValueSpec<?> otherLVS;
        if (this == otherObj) {
            return true;
        }
        if (otherObj instanceof LongValueSpec) {
            otherLVS = (LongValueSpec<?>) otherObj;
            return (Util.refEquals(this.lowerBoundInclusive, otherLVS.lowerBoundInclusive)
                    && Util.refEquals(this.upperBoundExclusive, otherLVS.upperBoundExclusive)
                    && (this.typeMin == otherLVS.typeMin)
                    && (this.typeMax == otherLVS.typeMax));
        }
        return false;
    }
    
    @Override
    protected LongValueSpec<P> self() { return this; }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public LongValueSpec(final P parent, final Long typeMin, final Long typeMax) throws IllegalArgumentException, ArithmeticException {
        super(parent);
        if (typeMin == null) {
            this.typeMin = TYPEMIN_DEFAULT;
        } else {
            this.typeMin = typeMin.longValue();
        }
        if (typeMax == null) {
            this.typeMax = TYPEMAX_DEFAULT;
        } else {
            this.typeMax = typeMax.longValue();
        }
        validateBoundsIncInc(Long.valueOf(this.typeMin), Long.valueOf(this.typeMax));
        this.lowerBoundInclusive = null;
        this.upperBoundExclusive = null;
    }
    public LongValueSpec(final P parent, final long typeMin, final long typeMax) throws IllegalArgumentException, ArithmeticException {
        this(parent, Long.valueOf(typeMin), Long.valueOf(typeMax));
    }
    public LongValueSpec(final P parent) throws IllegalArgumentException, ArithmeticException {
        this(parent, null, null);
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
