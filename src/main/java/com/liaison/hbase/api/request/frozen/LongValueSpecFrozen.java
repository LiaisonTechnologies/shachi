/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.frozen;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface LongValueSpecFrozen {
    long getTypeMin();
    long getTypeMax();
    Long getLowerBoundInclusive();
    Long getUpperBoundExclusive();
    boolean isLowerBounded();
    boolean isUpperBounded();
    boolean isBounded();
    /**
     * If this long-value specification refers to a single value (rather than a range), then return
     * that value; otherwise, return null.
     * @return Long object indicating the single-element long-int to which this range refers, or
     * null if this range encompasses multiple elements.
     */
    Long singleValue();

    /**
     * Indicate whether this long-value specification refers to a single-element range. Equivalent
     * to {@code (singleValue() != null)}.
     * @return {@code (singleValue() != null)}; i.e. {@code true} if this is a single-element long-
     * int range, {@code false} otherwise.
     */
    boolean isSingleValue();
}
