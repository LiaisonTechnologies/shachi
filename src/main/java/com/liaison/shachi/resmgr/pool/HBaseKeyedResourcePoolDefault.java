/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.resmgr.pool;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseKeyedResourcePoolDefault<K, R> extends GenericKeyedObjectPool<K, R> implements HBaseKeyedResourcePool<K, R> {
    public HBaseKeyedResourcePoolDefault(final KeyedPooledObjectFactory<K, R> factory, final GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }
    public HBaseKeyedResourcePoolDefault(final KeyedPooledObjectFactory<K, R> factory) {
        super(factory);
    }
}
