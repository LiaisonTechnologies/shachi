/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr.pool;

import org.apache.commons.pool2.ObjectPool;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface HBaseResourcePool<R> extends ConfigurableNonKeyedResourcePool<R>, StatsAwareResourcePool<R>, ObjectPool<R> {

}
