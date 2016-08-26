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
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.model.QualModel;

/**
 * Represents operations applicable to a single column in HBase, as indicated by the combination
 * of a single family ({@link FamilyModel}) and a single qualifier ({@link QualModel}), when an API
 * operation is in a <strong>fluid</strong> state (i.e. changes still allowed).
 * @see {@link SpecState} 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface ColSpecFluid<C extends ColSpecFluid<C>> {
    /**
     * TODO: javadoc
     * @param handle
     * @return
     * @throws IllegalStateException
     */
    C handle(final Object handle) throws IllegalStateException;
    /**
     * Specify that this column reference refers to the given HBase column family.
     * <br><br>
     * <strong>Cardinality:</strong> As this column reference may refer only to a single column
     * family, implementations must throw IllegalStateException if the column family has already
     * been assigned (e.g. by a previous invocation of this method).
     * @param family {@link FamilyModel} representing the HBase column family to which this column
     * reference refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link FamilyModel} has already been assigned for this
     * column reference, or if this operation is not in fluid state
     * @throws IllegalArgumentException if the provided {@link FamilyModel} is null or otherwise
     * invalid
     */
    C fam(final FamilyHB family) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify that this column reference refers to the given HBase column qualifier.
     * <br><br>
     * <strong>Cardinality:</strong> As this column reference may refer only to a single column
     * qualifier, implementations must throw IllegalStateException if the column qualifier has
     * already been assigned (e.g. by a previous invocation of this method).
     * @param qual {@link QualModel} representing the HBase column qualifier to which this column
     * reference refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link QualModel} has already been assigned for this
     * column reference, or if this operation is not in fluid state
     * @throws IllegalArgumentException if the provided {@link QualModel} is null or otherwise
     * invalid
     */
    C qual(final QualHB qual) throws IllegalStateException, IllegalArgumentException;
}
