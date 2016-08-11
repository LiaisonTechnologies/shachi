/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.resmgr;

import com.liaison.commons.Util;
import com.liaison.commons.log.LogMeMaybe;
import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.HBaseInitializationException;
import com.liaison.shachi.exception.HBaseResourceAcquisitionException;
import com.liaison.shachi.exception.HBaseResourceReleaseException;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.resmgr.pool.HBaseKeyedResourcePool;
import com.liaison.shachi.resmgr.pool.HBaseKeyedResourcePoolDefault;
import com.liaison.shachi.resmgr.pool.HBaseResourcePool;
import com.liaison.shachi.resmgr.pool.HBaseResourcePoolDefault;
import com.liaison.shachi.resmgr.pool.StatsAwareKeyedResourcePool;
import com.liaison.shachi.resmgr.pool.StatsAwareResourcePool;
import com.liaison.shachi.resmgr.res.ManagedAdmin;
import com.liaison.shachi.resmgr.res.ManagedTable;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public enum PoolingHBaseResourceManager implements HBaseResourceManager {
    INSTANCE;
    
    /**
     * 
     * TODO
     * @author Branden Smith; Liaison Technologies, Inc.
     */
    private static final class PooledTableFactory extends BaseKeyedPooledObjectFactory<TableModel, ManagedTable> {

        private final HBaseContext context;
        
        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#create(java.lang.Object)
         */
        @Override
        public ManagedTable create(final TableModel model) throws HBaseResourceAcquisitionException {
            return ResourceManagerUtil.buildTableResource(INSTANCE, this.context, model);
        }

        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#wrap(java.lang.Object)
         */
        @Override
        public PooledObject<ManagedTable> wrap(final ManagedTable value) {
            return new DefaultPooledObject<>(value);
        }
        
        @Override
        /**
         * {@inheritDoc}
         * 
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#destroyObject(java.lang.Object, java.lang.Object)
         */
        public void destroyObject(final TableModel model, final PooledObject<ManagedTable> poolTable) throws HBaseResourceReleaseException {
            final ManagedTable mTable;
            if ((model != null) && (poolTable != null)) {
                mTable = poolTable.getObject();
                if (mTable != null) {
                    ResourceManagerUtil.destroyTableResource(mTable);
                }
            }
        }
        
        /**
         * 
         * @param context
         */
        private PooledTableFactory(final HBaseContext context) throws IllegalArgumentException {
            Util.ensureNotNull(context, this, "context", HBaseContext.class);
            this.context = context;
        }       
    }
    
    /**
     * 
     * TODO
     * @author Branden Smith; Liaison Technologies, Inc.
     */
    private static final class PooledAdminFactory extends BasePooledObjectFactory<ManagedAdmin> {

        private final HBaseContext context;
        
        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#create(java.lang.Object)
         */
        @Override
        public ManagedAdmin create() throws HBaseResourceAcquisitionException {
            return ResourceManagerUtil.buildAdminResource(INSTANCE, this.context);
        }

        /**
         * {@inheritDoc}
         * @see org.apache.commons.pool2.BaseKeyedPooledObjectFactory#wrap(java.lang.Object)
         */
        @Override
        public PooledObject<ManagedAdmin> wrap(final ManagedAdmin value) {
            return new DefaultPooledObject<>(value);
        }
        
        @Override
        /**
         * {@inheritDoc}
         * 
         * @see org.apache.commons.pool2.BasePooledObjectFactory
         */
        public void destroyObject(final PooledObject<ManagedAdmin> poolAdmin) throws HBaseResourceReleaseException {
            final ManagedAdmin mAdmin;
            if (poolAdmin != null) {
                mAdmin = poolAdmin.getObject();
                if (mAdmin != null) {
                    ResourceManagerUtil.destroyAdminResource(mAdmin);
                }
            }
        }
        
        /**
         * 
         * @param context
         */
        private PooledAdminFactory(final HBaseContext context) throws IllegalArgumentException {
            Util.ensureNotNull(context, this, "context", HBaseContext.class);
            this.context = context;
        }       
    }
    
    private static LogMeMaybe LOG = new LogMeMaybe(PoolingHBaseResourceManager.class);
    
    private static final boolean POOLDEFAULT_BLOCK_ON_POOL_EXHAUSTED = true;
    private static final int POOLDEFAULT_MAXWAIT_MILLI = 30000; // 30 seconds
    private static final int POOLDEFAULT_EVICTAFTER_MILLI = 60000; // 1 minute
    
    private static final Integer POOLDEFAULT_TABLE_POOL_MAXSIZE_OVERALL = null; // infinite
    private static final int POOLDEFAULT_TABLE_POOL_MAXSIZE = 25;
    private static final int POOLDEFAULT_TABLE_POOL_MAXIDLE = 5;
    private static final int POOLDEFAULT_TABLE_POOL_MINIDLE = 2;
    
    private static final int POOLDEFAULT_ADMIN_POOL_MAXSIZE = 10;
    private static final int POOLDEFAULT_ADMIN_POOL_MAXIDLE = 5;
    private static final int POOLDEFAULT_ADMIN_POOL_MINIDLE = 1;
    
    private static final String POOLNAME_TABLE_POOL = "table-pool";
    private static final String POOLNAME_ADMIN_POOL = "admin-pool";
    
    private final ConcurrentHashMap<HBaseContext, HBaseResourcePool<ManagedAdmin>> adminPoolMap;
    private final ConcurrentHashMap<HBaseContext, HBaseKeyedResourcePool<TableModel, ManagedTable>> tablePoolMap;

    private static <V> void logPoolStats(final String poolName, final StatsAwareResourcePool<?> pool) {
        try {
            LOG.trace(()->"[pool.stats:",
                      ()->poolName,
                      ()->"|ALL] borrowed=",
                      ()->Integer.valueOf(pool.getNumActive()),
                      ()->",available=",
                      ()->Integer.valueOf(pool.getNumIdle()),
                      ()->",max=",
                      ()->Integer.valueOf(pool.getMaxTotal()));
        } catch (Exception exc) {
            // Logging-related; do not propagate
            LOG.trace(exc, ()->"Failed to log stats for pool: ", ()->poolName);
        }
    }
    
    private static <K, V> void logPoolStats(final String poolName, final StatsAwareKeyedResourcePool<K,?> keyedPool, final K key) {
        try {
            LOG.trace(()->"[pool.stats:",
                      ()->poolName,
                      ()->"|key=",
                      ()->key,
                      ()->"] borrowed=",
                      ()->Integer.valueOf(keyedPool.getNumActive(key)),
                      ()->",available=",
                      ()->Integer.valueOf(keyedPool.getNumIdle(key)),
                      ()->",max=",
                      ()->Integer.valueOf(keyedPool.getMaxTotalPerKey()));
            logPoolStats(poolName, keyedPool);
        } catch (Exception exc) {
            // Logging-related; do not propagate
            LOG.trace(exc, ()->"Failed to log stats for pool: ", ()->poolName);
        }
    }
    
    private static HBaseKeyedResourcePool<TableModel, ManagedTable> buildTablePool(final HBaseContext context) {
        final PooledTableFactory factory;
        final HBaseKeyedResourcePool<TableModel, ManagedTable> pool;
        
        factory = new PooledTableFactory(context);
        pool = new HBaseKeyedResourcePoolDefault<TableModel, ManagedTable>(factory);
        
        pool.setBlockWhenExhausted(POOLDEFAULT_BLOCK_ON_POOL_EXHAUSTED);
        pool.setMaxWaitMillis(POOLDEFAULT_MAXWAIT_MILLI);
        pool.setSoftMinEvictableIdleTimeMillis(POOLDEFAULT_EVICTAFTER_MILLI);
        pool.setMaxIdlePerKey(POOLDEFAULT_TABLE_POOL_MAXIDLE);
        pool.setMinIdlePerKey(POOLDEFAULT_TABLE_POOL_MINIDLE);
        pool.setMaxTotalPerKey(POOLDEFAULT_TABLE_POOL_MAXSIZE);
        if (POOLDEFAULT_TABLE_POOL_MAXSIZE_OVERALL != null) {
            pool.setMaxTotal(POOLDEFAULT_TABLE_POOL_MAXSIZE_OVERALL.intValue());
        }
        
        // TODO: allow the HBaseContext to override pool factory defaults
        
        return pool;
    }

    private static HBaseResourcePool<ManagedAdmin> buildAdminPool(final HBaseContext context) {
        final PooledAdminFactory factory;
        final HBaseResourcePool<ManagedAdmin> pool;
        
        factory = new PooledAdminFactory(context);
        pool = new HBaseResourcePoolDefault<ManagedAdmin>(factory);
        
        pool.setBlockWhenExhausted(POOLDEFAULT_BLOCK_ON_POOL_EXHAUSTED);
        pool.setMaxWaitMillis(POOLDEFAULT_MAXWAIT_MILLI);
        pool.setSoftMinEvictableIdleTimeMillis(POOLDEFAULT_EVICTAFTER_MILLI);
        pool.setMaxIdle(POOLDEFAULT_ADMIN_POOL_MAXIDLE);
        pool.setMinIdle(POOLDEFAULT_ADMIN_POOL_MINIDLE);
        pool.setMaxTotal(POOLDEFAULT_ADMIN_POOL_MAXSIZE);
        
        // TODO: allow the HBaseContext to override pool factory defaults
        
        return pool;
    }
    
    /**
     * TODO
     * @param context
     * @param poolMap
     * @param poolBuilder
     * @return
     */
    private static <P extends StatsAwareResourcePool<?>> P obtainPool(final HBaseContext context, final Map<HBaseContext, P> poolMap, final Function<HBaseContext, P> poolBuilder) throws HBaseInitializationException {
        final String logMsg;
        P pool;
        
        pool = poolMap.get(context);
        if (pool != null) {
            return pool;
        } else {
            poolMap.putIfAbsent(context, poolBuilder.apply(context));
            pool = poolMap.get(context);
            if (pool != null) {
                logPoolStats("fds", pool);
                return pool;
            } else {
                /* 
                 * should never happen, since there should be no other ways to modify poolMap, except
                 * by this method
                 */
                logMsg =
                    "PROBABLE CONCURRENCY FAILURE: Unable to obtain object pool for "
                    + HBaseContext.class.getSimpleName()
                    + " '"
                    + context.getId()
                    + "', either by acquiring the existing one or by creating a new one";
                throw new HBaseInitializationException(logMsg);
            }
        }
    }

    private static <P> P obtainPool(final HBaseContext context, final Map<HBaseContext, P> poolMap) throws HBaseInitializationException {
        final String logMsg;
        final P pool;
        
        pool = poolMap.get(context);
        if (pool == null) {
            logMsg = "Object pool for "
                     + HBaseContext.class.getSimpleName()
                     + " '"
                     + context.getId()
                     + "' does not exist, and invoke context does not permit pool creation";
            throw new HBaseInitializationException(logMsg);
        }
        return pool;
    }
    
    @Override
    public ManagedTable borrow(final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final HBaseKeyedResourcePool<TableModel, ManagedTable> pool;
        final ManagedTable mTable;
        
        logMethodName =
            LOG.enter(()->"borrow(context.id=",
                      ()->context.getId(),
                      ()->",model=",
                      ()->model,
                      ()->")");
        
        try {
            pool = obtainPool(context, this.tablePoolMap, PoolingHBaseResourceManager::buildTablePool);
            mTable = pool.borrowObject(model);
            LOG.trace(logMethodName, ()->"obtained: ", ()->mTable);
            logPoolStats(POOLNAME_TABLE_POOL, pool, model);
            return mTable;
        } catch (Exception exc) {
            logMsg = "Failed to obtain "
                     + ManagedTable.class.getSimpleName()
                     + " from pool; "
                     + exc.toString();
            LOG.error(exc, logMethodName, ()->logMsg);
            throw new HBaseResourceAcquisitionException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }

    @Override
    public void release(final ManagedTable mTable) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final TableModel model;
        final HBaseKeyedResourcePool<TableModel, ManagedTable> pool;
        
        logMethodName =
            LOG.enter(()->"release(context.id=",
                      ()->mTable,
                      ()->")");
        
        try {
            pool = obtainPool(mTable.getContext(), this.tablePoolMap);
            model = mTable.getModel();
            pool.returnObject(model, mTable);
            LOG.trace(logMethodName, ()->"released: ", ()->mTable);
            logPoolStats(POOLNAME_TABLE_POOL, pool, model);
        } catch (Exception exc) {
            logMsg = "RESOURCE LEAK: Failed to release "
                     + ManagedTable.class.getSimpleName()
                     + " to pool; "
                     + exc.toString();
            LOG.error(exc, logMethodName, ()->logMsg);
            throw new HBaseResourceReleaseException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }

    @Override
    public ManagedAdmin borrowAdmin(final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final HBaseResourcePool<ManagedAdmin> pool;
        final ManagedAdmin mAdmin;
        
        logMethodName =
            LOG.enter(()->"borrowAdmin(context.id=",
                      ()->context.getId(),
                      ()->")");
        
        pool = obtainPool(context, this.adminPoolMap, PoolingHBaseResourceManager::buildAdminPool);
        try {
            mAdmin = pool.borrowObject();
            LOG.trace(logMethodName, ()->"obtained: ", ()->mAdmin);
            logPoolStats(POOLNAME_ADMIN_POOL, pool);
            return mAdmin;
        } catch (Exception exc) {
            logMsg = "Failed to obtain "
                     + ManagedAdmin.class.getSimpleName()
                     + " from pool; "
                     + exc.toString();
            LOG.error(exc, logMethodName, ()->logMsg);
            throw new HBaseResourceAcquisitionException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }

    @Override
    public void releaseAdmin(final ManagedAdmin mAdmin) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final HBaseResourcePool<ManagedAdmin> pool;
        
        logMethodName =
            LOG.enter(()->"release(context.id=",
                      ()->mAdmin,
                      ()->")");

        try {
            pool = obtainPool(mAdmin.getContext(), this.adminPoolMap);
            pool.returnObject(mAdmin);
            LOG.trace(logMethodName, ()->"released: ", ()->mAdmin);
            logPoolStats(POOLNAME_ADMIN_POOL, pool);
        } catch (Exception exc) {
            logMsg = "RESOURCE LEAK: Failed to release "
                     + ManagedAdmin.class.getSimpleName()
                     + " to pool; "
                     + exc.toString();
            LOG.error(exc, logMethodName, ()->logMsg);
            throw new HBaseResourceReleaseException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }
    
    private PoolingHBaseResourceManager() {
        this.adminPoolMap = new ConcurrentHashMap<>();
        this.tablePoolMap = new ConcurrentHashMap<>();
    }
}
