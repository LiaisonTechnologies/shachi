/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.resmgr.pool;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface StatsAwareKeyedResourcePool<K, R> extends StatsAwareResourcePool<R> {
    int getNumActive(K key);
    int getNumIdle(K key);
    int getMaxTotalPerKey();
}
