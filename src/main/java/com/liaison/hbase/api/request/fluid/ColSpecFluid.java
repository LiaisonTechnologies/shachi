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
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;

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
     * Specify that this column reference refers to the given HBase column family.
     * <br><br>
     * <strong>Cardinality:</strong> As this column reference may refer only to a single column
     * family, implementations must throw IllegalStateException if the column family has already
     * been assigned (e.g. by a previous invocation of this method).
     * @param family {@link FamilyModel} representing the HBase column family to which this column
     * reference refers.
     * @return this instance (for fluent/chaining API)
     * @throws IllegalStateException if a {@link FamilyModel} has already been assigned for this
     * column reference
     * @throws IllegalArgumentException if the provided {@link FamilyModel} is null or otherwise
     * invalid
     */
    C fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException;
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
     * column reference
     * @throws IllegalArgumentException if the provided {@link QualModel} is null or otherwise
     * invalid
     */
    C qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException;
}
