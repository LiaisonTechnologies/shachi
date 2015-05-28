/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid.fluent;

import com.liaison.hbase.api.request.fluid.CriteriaSpecFluid;
import com.liaison.hbase.api.request.fluid.RowSpecFluid;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface RowSpecFluent<R extends RowSpecFluent<R, P>, P> extends RowSpecFluid<R>, CriteriaSpecFluid<P> {

}
