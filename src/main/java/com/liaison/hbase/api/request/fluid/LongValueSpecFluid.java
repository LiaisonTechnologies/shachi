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
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface LongValueSpecFluid<P> extends CriteriaSpecFluid<P> {
    LongValueSpecFluid<P> gt(long value) throws ArithmeticException;
    LongValueSpecFluid<P> ge(long value) throws ArithmeticException;
    LongValueSpecFluid<P> eq(long value) throws ArithmeticException;
    LongValueSpecFluid<P> lt(long value) throws ArithmeticException;
    LongValueSpecFluid<P> le(long value);
}
