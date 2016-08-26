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

import com.liaison.shachi.api.request.fluid.fluent.LongValueSpecFluent;

/**
 * An instance of {@link ColSpecFluid} intended for use in HBase read operations.
 * <br><br>
 * As is the case for {@link ColSpecFluid}, operations defined here are intended for operations in
 * fluid state (i.e. those which are still undergoing specification via the API).
 * @see {@link ColSpecFluid}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface ColSpecReadFluid<C extends ColSpecReadFluid<C>> extends ColSpecFluid<C> {
    /**
     * TODO
     * @param version
     * @return
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    LongValueSpecFluent<C> version() throws IllegalStateException, IllegalArgumentException;
    /**
     * TODO
     * @param version
     * @return
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    C version(final long version) throws IllegalStateException, IllegalArgumentException;
    /**
     * Specify that reading the given column family+qualifier combination is not required, and
     * therefore operations which own this reference should <em>not</em> throw an Exception if it
     * is not present or not readable.
     * <br><br>
     * <strong>Cardinality:</strong> Should only be invoked either <strong>zero times</strong> (for
     * required columns) or <strong>one time</strong> (for optional columns); implementations must
     * throw IllegalStateException upon repeated invocations.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if this method has already been invoked, or if this operation
     * is not in fluid state
     */
    C optional() throws IllegalStateException;
}
