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

import com.liaison.shachi.dto.Empty;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.QualModel;

/**
 * Extension of {@link ColSpecFluid} to allow an exact HBase cell with a given value to be
 * specified, for the purpose of evaluating a condition predicated upon that cell. Specifies a row
 * key ({@link RowKey}) and either a non-null value ({@link Value}) or a null reference ({@link
 * Empty}), in addition to a column family ({@link FamilyModel}) and qualifier ({@link QualModel})
 * (as prescribed by {@link ColSpecFluid}).
 * <br><br>
 * As is the case for {@link ColSpecFluid}, operations defined here are intended for operations in
 * fluid state (i.e. those which are still undergoing specification via the API).
 * @see {@link ColSpecFluid}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface CondSpecFluid<C extends CondSpecFluid<C>> extends ColSpecFluid<C> {
    /**
     * Specify that the condition to be evaluated refers to a cell with the given {@link RowKey}
     * row key.
     * <br><br>
     * <strong>Cardinality:</strong> As this condition may refer only to a single cell on a single
     * row, implementations must throw IllegalStateException if the row key assigned to the
     * condition has already been assigned.
     * @param rowKey {@link RowKey} indicating the row key of the cell on which this condition is
     * to be evaluated.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link RowKey} has already been assigned for this
     * condition, or if this operation is not in fluid state
     * @throws IllegalArgumentException if the provided {@link RowKey} is null or otherwise invalid
     */
    C row(RowKey rowKey) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify that the condition to be evaluated refers to a cell with the given {@link Value}
     * non-null value. Note that this operation and {@link #empty(Empty)} are exclusive of one
     * another; exactly 1 of the 2 operations must be invoked.
     * <br><br>
     * <strong>Cardinality:</strong> As this condition may refer only to a single cell with a
     * a single value, implementations must throw IllegalStateException if a value (either non-null
     * via this method or null/empty via {@link #empty(Empty)}) has already been assigned.
     * @param value {@link Value} representing the non-null value to which this condition refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if either a non-null value ({@link Value}) or a null value
     * ({@link Empty}) has already been assigned for this condition, or if this operation is not in
     * fluid state
     * @throws IllegalArgumentException if the provided {@link Value} is null or otherwise invalid
     */
    C value(Value value) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify that the condition to be evaluated refers to a cell a null/empty value, as
     * represented by the given {@link Empty}. Note that this operation and {@link #value(Value)}
     * are exclusive of one another; exactly 1 of the 2 operations must be invoked.
     * <br><br>
     * <strong>Cardinality:</strong> As this condition may refer only to a single cell with a
     * a single value, implementations must throw IllegalStateException if a value (either null/
     * empty via this method or non-null via {@link #empty(Empty)}) has already been assigned.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if either a null/empty value ({@link Empty}) or a non-null
     * value ({@link Value}) has already been assigned for this condition, or if this operation is
     * not in fluid state
     */
    C empty() throws IllegalStateException;
}
