/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.fluid.ReadOpSpecFluid;
import com.liaison.hbase.api.request.frozen.ReadOpSpecFrozen;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ReadOpSpec<Z> extends ReadOpSpecFluid<Z>, ReadOpSpecFrozen {

}
