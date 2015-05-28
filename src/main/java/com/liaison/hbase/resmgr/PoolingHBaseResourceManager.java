/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.exception.HBaseResourceReleaseException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.resmgr.res.ManagedAdmin;
import com.liaison.hbase.resmgr.res.ManagedTable;
import com.liaison.hbase.util.Util;


/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public enum PoolingHBaseResourceManager implements HBaseResourceManager {
    INSTANCE;
    
    private static final class PooledTableFactory extends BaseKeyedPooledObjectFactory<TableModel, ManagedTable> {

        private final HBaseContext context;
        
        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#create(java.lang.Object)
         */
        @Override
        public ManagedTable create(final TableModel model) throws Exception {
            return ResourceManagerUtil.borrow(INSTANCE, this.context, model);
        }

        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#wrap(java.lang.Object)
         */
        @Override
        public PooledObject<ManagedTable> wrap(final ManagedTable value) {
            return new DefaultPooledObject<ManagedTable>(value);
        }
        
        private PooledTableFactory(final HBaseContext context) {
            Util.ensureNotNull(context, this, "context", HBaseContext.class);
            this.context = context;
        }
                
    }
    
    private final ConcurrentHashMap<HBaseContext, ObjectPool<HBaseAdmin>> adminPoolMap;
    private final ConcurrentHashMap<HBaseContext, KeyedObjectPool<TableModel, ManagedTable>> tablePoolMap;

    private <P> P getPool(final HBaseContext context, final Map<HBaseContext, P> poolMap, final Function<HBaseContext, P> poolBuilder) {
        P pool;
        
        pool = poolMap.get(context);
        if (pool == null) {
            poolMap.putIfAbsent(context, poolBuilder.apply(context));
        }
        pool = poolMap.get(context);
        if (pool == null) {
            // should never happen
            throw new RuntimeException("Unable to obtain object pool for "
                                       + HBaseContext.class.getSimpleName()
                                       + " '"
                                       + context.getId()
                                       + "', either by acquiring the existing one or by creating "
                                       + "a new one; PROBABLE CONCURRENCY FAILURE");
        }
        return pool;
    }
    
    @Override
    public ManagedTable borrow(final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final KeyedObjectPool<TableModel, ManagedTable> pool;
        
        pool =
            getPool(context,
                    this.tablePoolMap,
                    (ctx) -> {
                        final PooledTableFactory factory;
                        factory = new PooledTableFactory(context);
                        return new GenericKeyedObjectPool<TableModel, ManagedTable>(factory);
                    });
        try {
            return pool.borrowObject(model);
        } catch (Exception exc) {
            // TODO
            throw new HBaseResourceAcquisitionException("TODO", exc);
        }
    }

    @Override
    public void release(final HBaseContext context, final TableModel model, final HTable table) throws HBaseResourceReleaseException, IllegalArgumentException {
        // TODO
    }

    @Override
    public ManagedAdmin borrowAdmin(final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        // TODO
        return null;
    }

    @Override
    public void releaseAdmin(final HBaseContext context, final HBaseAdmin admin) throws HBaseResourceReleaseException, IllegalArgumentException {
        // TODO Auto-generated method stub
    }
    
    private PoolingHBaseResourceManager() {
        this.adminPoolMap = new ConcurrentHashMap<>();
        this.tablePoolMap = new ConcurrentHashMap<>();
    }
}
