package com.liaison.hbase.api.opspec;

import java.util.function.BiPredicate;

public class LongValueSpec<P extends CRUDOperationSpec<P>> extends CriteriaSpec<P> {
    
    public static final String SETNOT_LOWER_INC = "[";
    public static final String SETNOT_LOWER_EXC = "(";
    public static final String SETNOT_UPPER_INC = "]";
    public static final String SETNOT_UPPER_EXC = ")";
    
    private static final long TYPEMIN_DEFAULT = Long.MIN_VALUE;
    private static final long TYPEMAX_DEFAULT = Long.MAX_VALUE;
    
    private final long typeMin;
    private final long typeMax;
    
    private Long lowerBoundInclusive;
    private Long upperBoundExclusive;

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
    
    public long getTypeMin() {
        return this.typeMin;
    }
    public long getTypeMax() {
        return this.typeMax;
    }
    
    public Long getLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }
    public Long getUpperBoundExclusive() {
        return this.upperBoundExclusive;
    }

    /**
     * greater-than
     * @param value
     */
    public LongValueSpec<P> gt(long value) throws ArithmeticException {
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
    public LongValueSpec<P> ge(long value) throws ArithmeticException {
        setLowerBoundInclusive(Long.valueOf(value));
        return this;
    }
    
    /**
     * equal-to
     * @param value
     */
    public LongValueSpec<P> eq(long value) throws ArithmeticException {
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
    public LongValueSpec<P> lt(long value) throws ArithmeticException {
        setUpperBoundExclusive(Long.valueOf(value));
        return this;
    }
    
    /**
     * less-than-or-equal-to
     * @param value
     */
    public LongValueSpec<P> le(long value) {
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
}
