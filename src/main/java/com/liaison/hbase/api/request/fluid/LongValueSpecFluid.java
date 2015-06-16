/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

/**
 * Specification for a range of long-integer (long) values.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P> Type of the parent/owning operation
 */
public interface LongValueSpecFluid<F extends LongValueSpecFluid<F,P>, P> {
    /**
     * <strong>Greater-Than</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V > value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>greater than</strong>
     * this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F gt(long value) throws ArithmeticException;
    /**
     * <strong>Greater-Than-Or-Equal</strong>: require that values <code>V</code> in the range
     * satisfy
     * <br><br>
     * <code>
     * V >= value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>greater than or equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F ge(long value) throws ArithmeticException;
    /**
     * <strong>Equal</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V == value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that the range consist of a single element, <strong>equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F eq(long value) throws ArithmeticException;
    /**
     * <strong>Lesser-Than</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V < value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>lesser than</strong>
     * this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F lt(long value) throws ArithmeticException;
    /**
     * <strong>Lesser-Than-Or-Equal</strong>: require that values <code>V</code> in the range
     * satisfy
     * <br><br>
     * <code>
     * V <= value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>lesser than or equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F le(long value) throws ArithmeticException;
}
