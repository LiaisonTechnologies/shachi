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
     * Specify that reading the given column family+qualifier combination is not required, and
     * therefore operations which own this reference should <em>not</em> throw an Exception if it
     * is not present or not readable.
     * <br><br>
     * <strong>Cardinality:</strong> Should only be invoked either <strong>zero times</strong> (for
     * required columns) or <strong>one time</strong> (for optional columns); implementations must
     * throw IllegalStateException upon repeated invocations.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if this method has already been invoked
     */
    C optional() throws IllegalStateException;
}
