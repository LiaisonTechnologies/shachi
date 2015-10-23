/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.request.fluid.fluent.ColSpecWriteFluent;
import com.liaison.hbase.api.request.fluid.fluent.CondSpecFluent;
import com.liaison.hbase.api.request.fluid.fluent.RowSpecFluent;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Specifies an <strong>HBase write operation</strong> while it is in a fluid state (i.e. wherein
 * it is still being specified  by the fluent/chaining API).
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z> The operation result type which will be produced when the owning
 * {@link OperationController} executes {@link OperationController#exec()}.
 */
public interface WriteOpSpecFluid<Z> extends OpSpecFluid<Z> {
    /**
     * Specify the table and row key corresponding to the HBase row on which the HBase write
     * operation specified by this spec will write.
     * <br><br>
     * <strong>Cardinality:</strong> The row specificaion must be provided <strong>exactly
     * once</strong>; implementations must throw IllegalStateException on repeated invocations of
     * this method.
     * @return a {@link RowSpecFluent} instance owned by this write operation specification,
     * whereby the table and row targeted by this write operation may be specified.
     * @throws IllegalStateException if a table+row combination is already associated with this
     * write operation (e.g. by a previous invocation of this method), or if the operation spec is
     * not in a fluid state
     * @see {@link ReadOpSpecFluid#from()} (equivalent operation on read)
     */
    RowSpecFluent<?, ? extends WriteOpSpecFluid<Z>> on() throws IllegalArgumentException, IllegalStateException;
    /**
     * Specify a condition such that the HBase write operation specified by this spec will execute
     * if-and-only-if the condition evaluates to true. This method is the means by which
     * check-and-put (or put-if-absent) operations may be implemented by this API.
     * <br><br>
     * <strong>Cardinality:</strong> A condition may be specified <strong>at most once</strong>;
     * implementations must throw IllegalStateException on repeated invocations of this method.
     * @return a {@link CondSpecFluent} instance owned by this write operation specification,
     * whereby the parameters of the conditional write may be specified.
     * @throws IllegalStateException if a condition is already associated with this write operation
     * (e.g. by a previous invocation of this method), or if the operation spec is not in a fluid
     * state
     */
    CondSpecFluent<?, ? extends WriteOpSpecFluid<Z>> given() throws IllegalStateException;
    /**
     * Add a cell (consisting of a column specification, the value to be assigned that column, and
     * optionally a timestamp) to the HBase write operation specification.
     * <br><br>
     * <strong>Cardinality:</strong> This method or {@link #withAllOf(Iterable, BiConsumer)} must
     * be invoked <strong>at least once</strong>, in order to specify at least one column+value for
     * the write operation.
     * @return a {@link ColSpecWriteFluent} instance owned by this write operation specification,
     * whereby the column and value to be written may be specified.
     * @see {@link ReadOpSpecFluid#with()} (equivalent operation on read)
     */
    ColSpecWriteFluent<?, ? extends WriteOpSpecFluid<Z>> with() throws IllegalStateException;
    /**
     * TODO: javadoc
     * @param handle
     * @return
     * @throws IllegalStateException
     */
    ColSpecWriteFluent<?, ? extends WriteOpSpecFluid<Z>> with(Object handle) throws IllegalStateException;
    /**
     * Add a series of column+value specifications to the list of cells which the HBase write
     * operation specified by this spec will write. Unlike {@link #with()}, which returns a
     * {@link ColSpecWriteFluent} instance allowing the cell write parameters for each cell to be
     * specified manually, this method expects a collection of arbitrarily-typed objects and
     * a two-argument consumer method/lambda, which in turn accepts an element from the list as its
     * first parameter, and an instance of {@link ColSpecWriteFluid} as its second parameter. The
     * implementation must iterate through the collection (in the default iteration order specified
     * by the {@link Iterable} implementation, then provide the collection element and a new
     * instance of {@link ColSpecWriteFluid} owned by this write operation specification to the
     * consumer. The client-specified consumer, then, is responsible for invoking the appropriate
     * API methods on the {@link ColSpecWriteFluid} instance to transform the collection element to
     * a cell specification to be added to the list of cells which the HBase write operation
     * specified by this spec will write.
     * <br><br>
     * This method is useful in order to internalize iteration over a collection of elements which
     * indicate the cells which this write operation should write. For example, a client with a
     * map of keys to values indicating the names of column qualifiers and their associated values
     * might use this method in conjunction with a lambda which transforms a Map.Entry into a
     * column identifier and value the context of a {@link ColSpecWriteFluid} instance, in order to
     * specify that the write operation should write all of the column+value combinations.
     * <br><br>
     * <strong>Cardinality:</strong> This method or {@link #with()} must be invoked <strong>at
     * least once</strong>, in order to specify at least one cell to be written.
     * @param sourceData a collection (Iterable) of elements of arbitrary type which are to be
     * transformed into cell specifications ({@link ColSpecWriteFluid}) indicating columns+values
     * which the HBase write operation specified by this spec is to write
     * @param dataToColumnGenerator two-argument consumer method/lambda (BiConsumer) which consumes
     * an element from the sourceData collection and a new {@link ColSpecWriteFluid} owned by this
     * write operation specification, then modifies the {@link ColSpecWriteFluid} to add cell data
     * and meta-data as indicated by the sourceData element
     * @return this instance (for fluent/chaining API)
     * @see {@link ReadOpSpecFluid#withAllOf(Iterable, BiConsumer)} (equivalent operation on read)
     */
    <X> WriteOpSpecFluid<Z> withAllOf(Iterable<X> sourceData, BiConsumer<X, ColSpecWriteFluid<?>> dataToColumnGenerator);
    /**
     * TODO: javadoc
     * @param sourceData
     * @param dataToColumnGenerator
     * @param <X>
     * @return
     */
    <X> WriteOpSpecFluid<Z> withAllOf(Iterable<X> sourceData, BiFunction<X, ColSpecWriteFluid<?>, Object> dataToColumnGenerator);
    /**
     * Specify a time-to-live (TTL) value for all cells written as part of this write operation.
     * Assuming that TTL is supported by the underlying HBase implementation, the associated cells
     * will be purged by the first major compaction which occurs after the current time plus the
     * specified TTL value.
     * @param ttlValue the TTL value; after the amount of time indicated by this value multiplied
     *                 by the TTL unit specified by the other parameter, the cell(s) written will
     *                 be eligible to be purged via an HBase major compaction
     * @param ttlUnit the time unit by which the TTL value is multiplied to establish the TTL. Note
     *                that since HBase's underlying API only resolves at millisecond-granularity,
     *                any time references with TimeUnit.MICROSECOND or TimeUnit.NANOSECONDS
     *                precision will be rounded down to the nearest full millisecond.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a TTL has already been assigned for this write, or if this
     * operation is not in fluid state
     * @throws IllegalArgumentException if ttlValue is < 0, or if ttlUnit is null
     */
    WriteOpSpecFluid<Z> keepFor(final long ttlValue, final TimeUnit ttlUnit) throws IllegalStateException, IllegalArgumentException;
}
