/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.resmgr.pool;

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
