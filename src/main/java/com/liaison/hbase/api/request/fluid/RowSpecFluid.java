/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import com.liaison.hbase.api.request.impl.SpecState;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.TableModel;

/**
 * Represents the operation specification applicable to a single row in a single table in HBase.
 * Because operation consistency in HBase is guaranteed at the row level, this entity effectively
 * keys the consistency/atomicity of the operation specifications which own it. The operations
 * specify here are available exclusively when the owning operation are in a <strong>fluid</strong>
 * state (i.e. changes still allowed).
 * @see {@link SpecState} 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <R> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface RowSpecFluid<R extends RowSpecFluid<R>> {
    /**
     * Specify that this row/table reference refers to the given HBase table.
     * <br><br>
     * <strong>Cardinality:</strong> As this row/table reference may refer only to a single row in
     * a single table, implementations must throw IllegalStateException if the table reference has
     * already been assigned (e.g. by a previous invocation of this method).
     * @param table {@link TableModel} representing the HBase table to which this row/table
     * reference refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link TableModel} has already been assigned for this
     * row/table reference, or if this operation is not in fluid state
     * @throws IllegalArgumentException if the provided {@link TableModel} is null or otherwise
     * invalid
     */
    R tbl(final TableModel table) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify that this row/table reference refers to the given row key.
     * <br><br>
     * <strong>Cardinality:</strong> As this row/table reference may refer only to a single row in
     * a single table, implementations must throw IllegalStateException if the row key has already
     * been assigned (e.g. by a previous invocation of this method).
     * @param rowKey {@link RowKey} representing the row key to which this row/table reference
     * refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link RowKey} has already been assigned for this
     * row/table reference, or if this operation is not in fluid state
     * @throws IllegalArgumentException if the provided {@link RowKey} is null or otherwise invalid
     */
    R row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException;
}
