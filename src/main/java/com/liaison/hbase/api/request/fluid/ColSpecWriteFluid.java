/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.Value;

import java.util.concurrent.TimeUnit;

/**
 * An instance of {@link ColSpecFluid} intended for use in HBase write operations.
 * <br><br>
 * As is the case for {@link ColSpecFluid}, operations defined here are intended for operations in
 * fluid state (i.e. those which are still undergoing specification via the API).
 * @see {@link ColSpecFluid}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface ColSpecWriteFluid<C extends ColSpecWriteFluid<C>> extends ColSpecFluid<C> {
    /**
     * Specify a time-to-live (TTL) value for the cell containing the value to be written. Assuming
     * that TTL is supported by the underlying HBase implementation, the associated cell will be
     * purged by the first major compaction which occurs after the current time plus the specified
     * TTL value.
     * @param ttlValue the TTL value; after the amount of time indicated by this value multiplied
     *                 by the TTL unit specified by the other parameter, the cell(s) written will
     *                 be eligibile to be purged via an HBase major compaction
     * @param ttlUnit the time unit by which the TTL value is multiplied to establish the TTL. Note
     *                that since HBase's underlying API only resolves at millisecond-granularity,
     *                any time references with TimeUnit.MICROSECOND or TimeUnit.NANOSECONDS
     *                precision will be rounded down to the nearest full millisecond.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a TTL has already been assigned for this write, or if this
     * operation is not in fluid state
     * @throws IllegalArgumentException if ttlValue is < 0, or if ttlUnit is null
     */
    C ttl(final long ttlValue, final TimeUnit ttlUnit) throws IllegalStateException, IllegalArgumentException;
    /**
     * TODO
     * @param version
     * @return
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    C version(final long version) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify a timestamp value to be assigned to the value to be written. If not specified, no
     * timestamp value will be passed to the underlying API, so HBase will use the default, system-
     * generated timestamp.
     * <br><br>
     * <strong>Cardinality:</strong> A single value may be assigned only one timestamp, so this
     * method may be invoked a maximum of 1 time. Implementations must throw IllegalStateException
     * upon repeated invocations.
     * @param ts the timestamp value to assign to the value to be written
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a timestamp has already been assigned for this write, or
     * if this operation is not in fluid state
     * @throws IllegalArgumentException if the timestamp is somehow invalid according to the
     * underlying implementation
     */
    C ts(final long ts) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify the given non-null value ({@link Value}) as the value to be written. Note that this
     * operation and {@link #empty(Empty)} are exclusive of one another; exactly 1 of the 2
     * operations must be invoked.
     * <br><br>
     * <strong>Cardinality:</strong> As only a single value may be written by this write
     * specification, implementations must throw IllegalStateException if a value (either non-null
     * via this method or null/empty via {@link #empty(Empty)}) has already been assigned.
     * @param value {@link Value} representing the non-null value to be written
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if either a non-null value ({@link Value}) or a null value
     * ({@link Empty}) has already been assigned for this write, or if this operation is not in
     * fluid state
     * @throws IllegalArgumentException if the provided {@link Value} is null or otherwise invalid
     */
    C value(final Value value) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify the given empty value ({@link Empty}) as the value to be written. Note that this
     * operation and {@link #value(Value)} are exclusive of one another; exactly 1 of the 2
     * operations must be invoked.
     * <br><br>
     * <strong>Cardinality:</strong> As only a single value may be written by this write
     * specification, implementations must throw IllegalStateException if a value (either null/
     * empty via this method or non-null via {@link #value(Value)}) has already been assigned.
     * <br><br>
     * TODO: Why does this method need a parameter? Empty is a singleton inheriting from
     * NullableValue, so why does the client have to supply it every time? It should be sufficient
     * to make this API call take zero parameters, and internally assign the NullableValue to the
     * singleton instance of Empty. Should investigate making this change.
     * <br><br>
     * @param empty {@link Empty} representing the null/empty value to be written
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if either a non-null value ({@link Value}) or a null value
     * ({@link Empty}) has already been assigned for this write, or if this operation is not in
     * fluid state
     * @throws IllegalArgumentException if the provided {@link Empty} is null or otherwise invalid
     */
    C empty(final Empty empty) throws IllegalStateException, IllegalArgumentException;

    /**
     * Specify an object to be serialized using the serializer associated with the model structure
     * for this column, if there is such a serializer. This method uses SpecUtil#identifySerializer
     * to prioritize which serializer to use to construct the byte array to be persisted, imposing
     * the following priority:
     * <ol>
     *     <li>qualifier (QualModel)</li>
     *     <li>family (FamilyModel)</li>
     *     <li>table (TableModel)</li>
     * </ol>
     * @param dataObj
     * @return
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    C content(final Object dataObj) throws IllegalStateException, IllegalArgumentException;
}
