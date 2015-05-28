/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid.fluent;

import com.liaison.hbase.api.request.fluid.ColSpecWriteFluid;
import com.liaison.hbase.api.request.fluid.CriteriaSpecFluid;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ColSpecWriteFluent<C extends ColSpecWriteFluent<C, P>, P> extends ColSpecWriteFluid<C>, CriteriaSpecFluid<P> {

}
