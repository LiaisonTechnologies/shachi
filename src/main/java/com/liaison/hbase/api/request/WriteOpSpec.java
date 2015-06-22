/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.fluid.WriteOpSpecFluid;
import com.liaison.hbase.api.request.frozen.WriteOpSpecFrozen;
import com.liaison.hbase.api.request.impl.SpecState;

/**
 * Specifies a <strong>WRITE</strong> operation, whose API consists of the union of ways in which
 * it may be <em>specified</em> while in a <em>fluid<state> and the ways in which it may be
 * <em>executed</em> (or referenced) while in a <em>frozen</em> state.
 * @see {@link SpecState}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface WriteOpSpec<Z> extends WriteOpSpecFluid<Z>, WriteOpSpecFrozen {

}
