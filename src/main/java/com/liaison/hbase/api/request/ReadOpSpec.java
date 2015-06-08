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
import com.liaison.hbase.api.request.impl.SpecState;

/**
 * Specifies a <strong>READ</strong> operation, whose API consists of the union of ways in which
 * it may be <em>specified</em> while in a <em>fluid<state> and the ways in which it may be
 * <em>executed</em> (or referenced) while in a <em>frozen</em> state.
 * @see {@link SpecState}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface ReadOpSpec<Z> extends ReadOpSpecFluid<Z>, ReadOpSpecFrozen { }
