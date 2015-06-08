/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid.fluent;

import com.liaison.hbase.api.request.fluid.CondSpecFluid;
import com.liaison.hbase.api.request.fluid.CriteriaSpecFluid;

/**
 * Denotes an API implementation of {@link CondSpecFluid} which can be retraced to its parent in
 * the API spec tree by calling {@link CriteriaSpecFluid#and()}.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> The type of the current element in the API/spec tree
 * @param <P> The type of the parent/owning element in the API/spec tree
 */
public interface CondSpecFluent<C extends CondSpecFluent<C, P>, P> extends CondSpecFluid<C>, CriteriaSpecFluid<P> {

}
