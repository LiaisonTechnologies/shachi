/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseResourcePoolDefault<R> extends GenericObjectPool<R> implements HBaseResourcePool<R> {
    public HBaseResourcePoolDefault(final PooledObjectFactory<R> factory, final GenericObjectPoolConfig config, final AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
    public HBaseResourcePoolDefault(final PooledObjectFactory<R> factory, final GenericObjectPoolConfig config) {
        super(factory, config);
    }
    public HBaseResourcePoolDefault(final PooledObjectFactory<R> factory) {
        super(factory);
    }
}
