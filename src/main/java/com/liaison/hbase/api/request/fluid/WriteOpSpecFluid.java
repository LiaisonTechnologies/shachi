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

import com.liaison.hbase.api.request.fluid.fluent.ColSpecWriteFluent;
import com.liaison.hbase.api.request.fluid.fluent.CondSpecFluent;
import com.liaison.hbase.api.request.fluid.fluent.RowSpecFluent;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z>
 */
public interface WriteOpSpecFluid<Z> extends OpSpecFluid<Z> {
    RowSpecFluent<?, ? extends WriteOpSpecFluid<Z>> on() throws IllegalArgumentException, IllegalStateException;
    CondSpecFluent<?, ? extends WriteOpSpecFluid<Z>> given() throws IllegalStateException;
    ColSpecWriteFluent<?, ? extends WriteOpSpecFluid<Z>> with() throws IllegalStateException;
    <X> WriteOpSpecFluid<Z> withAllOf(Iterable<X> sourceData, BiConsumer<X, ColSpecWriteFluid<?>> dataToColumnGenerator);
}
