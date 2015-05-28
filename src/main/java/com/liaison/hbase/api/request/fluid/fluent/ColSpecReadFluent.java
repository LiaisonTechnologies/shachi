/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid.fluent;

import com.liaison.hbase.api.request.fluid.ColSpecReadFluid;
import com.liaison.hbase.api.request.fluid.CriteriaSpecFluid;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ColSpecReadFluent<C extends ColSpecReadFluent<C, P>, P> extends ColSpecReadFluid<C>, CriteriaSpecFluid<P> {

}
