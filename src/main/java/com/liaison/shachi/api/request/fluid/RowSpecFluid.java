/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.api.request.fluid;

import com.liaison.shachi.api.request.impl.SpecState;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.model.TableModel;

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
