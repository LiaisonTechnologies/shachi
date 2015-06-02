/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import java.util.function.BiConsumer;

import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.request.fluid.fluent.ColSpecReadFluent;
import com.liaison.hbase.api.request.fluid.fluent.LongValueSpecFluent;
import com.liaison.hbase.api.request.fluid.fluent.RowSpecFluent;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ReadOpSpecFluid<Z> {
    LongValueSpecFluent<? extends ReadOpSpecFluid<Z>> atTime() throws IllegalStateException;
    RowSpecFluent<?, ? extends ReadOpSpecFluid<Z>> from() throws IllegalArgumentException, IllegalStateException;
    ColSpecReadFluent<?, ? extends ReadOpSpecFluid<Z>> with();
    <X> ReadOpSpecFluid<Z> withAllOf(Iterable<X> sourceData, BiConsumer<X, ColSpecReadFluid<?>> dataToColumnGenerator);
    OperationController<Z> then();
}
