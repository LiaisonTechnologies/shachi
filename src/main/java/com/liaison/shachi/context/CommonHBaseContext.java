/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.context;

import com.liaison.commons.Util;
import com.liaison.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.context.async.AsyncConfig;
import com.liaison.shachi.context.async.AsyncConfigDefault;
import com.liaison.shachi.resmgr.ResourceConnectTolerance;
import com.liaison.shachi.util.AbstractSelfRefBuilder;
import org.apache.hadoop.conf.Configuration;

import java.util.function.Supplier;

public abstract class CommonHBaseContext implements HBaseContext {

    protected abstract static class AbstractHBaseContextBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        private Object id;
        private AsyncConfig asyncConfig;
        private ResourceConnectTolerance resConnTol;
        private DefensiveCopyStrategy defensiveCopyStrategy;
        private Boolean createAbsentTables;
        private TableNamingStrategy tableNamingStrategy;
        private Supplier<Configuration> configProvider;

        public B id(final Object id) {
            this.id = id;
            return self();
        }
        public B asyncConfig(final AsyncConfig asyncConfig) {
            this.asyncConfig = asyncConfig;
            return self();
        }
        public B resourceConnectTolerance(final ResourceConnectTolerance resConnTol) {
            this.resConnTol = resConnTol;
            return self();
        }
        public B createAbsentTables(final boolean createAbsentTables) {
            this.createAbsentTables = Boolean.valueOf(createAbsentTables);
            return self();
        }
        public B defensiveCopyStrategy(final DefensiveCopyStrategy defensiveCopyStrategy) {
            this.defensiveCopyStrategy = defensiveCopyStrategy;
            return self();
        }
        public B tableNamingStrategy(final TableNamingStrategy tableNamingStrategy) {
            this.tableNamingStrategy = tableNamingStrategy;
            return self();
        }
        public B configProvider(final Supplier<Configuration> configProvider) {
            this.configProvider = configProvider;
            return self();
        }
        protected AbstractHBaseContextBuilder() {
            this.id = null;
            this.asyncConfig = null;
            this.resConnTol = null;
            this.defensiveCopyStrategy = null;
            this.createAbsentTables = null;
            this.tableNamingStrategy = null;
            this.configProvider = null;
        }
    }
    
    public static final DefensiveCopyStrategy DEFAULT_DEFENSIVE_COPY_STRATEGY = 
        DefensiveCopyStrategy.ALWAYS;
    public static final boolean DEFAULT_CREATE_ABSENT_TABLES = true;
    
    private final Object id;
    private final AsyncConfig asyncConfig;
    private final ResourceConnectTolerance resConnTol;
    private final DefensiveCopyStrategy defensiveCopyStrategy;
    private final boolean createAbsentTables;
    private final TableNamingStrategy tableNamingStrategy;
    private final Supplier<Configuration> configProvider;
    
    protected abstract TableNamingStrategy getDefaultTableNamingStrategy();
    
    @Override
    public final Object getId() {
        return this.id;
    }
    @Override
    public AsyncConfig getAsyncConfig() {
        return this.asyncConfig;
    }
    @Override
    public ResourceConnectTolerance getResourceConnectTolerance() {
        return this.resConnTol;
    }
    @Override
    public TableNamingStrategy getTableNamingStrategy() {
        return this.tableNamingStrategy;
    }
    @Override
    public DefensiveCopyStrategy getDefensiveCopyStrategy() {
        return this.defensiveCopyStrategy;
    }
    @Override
    public boolean doCreateAbsentTables() {
        return this.createAbsentTables;
    }
    @Override
    public Configuration getHBaseConfiguration() {
        return this.configProvider.get();
    }
    
    protected CommonHBaseContext(final AbstractHBaseContextBuilder<?,?> build) {
        Util.ensureNotNull(build.id, this, "id");
        this.id = build.id;
        Util.ensureNotNull(build.configProvider, this, "configProvider", Supplier.class);
        this.configProvider = build.configProvider;
        
        // Asynchronous execution is disabled by default
        if (build.asyncConfig == null) {
            this.asyncConfig = AsyncConfigDefault.getBuilder().disabled().build();
        } else {
            this.asyncConfig = build.asyncConfig;
        }
        
        if (build.resConnTol == null) {
            this.resConnTol = ResourceConnectTolerance.DEFAULT;
        } else {
            this.resConnTol = build.resConnTol;
        }
        if (build.defensiveCopyStrategy == null) {
            this.defensiveCopyStrategy = DEFAULT_DEFENSIVE_COPY_STRATEGY;
        } else {
            this.defensiveCopyStrategy = build.defensiveCopyStrategy;
        }
        if (build.tableNamingStrategy == null) {
            this.tableNamingStrategy = getDefaultTableNamingStrategy();
        } else {
            this.tableNamingStrategy = build.tableNamingStrategy;
        }
        if (build.createAbsentTables == null) {
            this.createAbsentTables = DEFAULT_CREATE_ABSENT_TABLES;
        } else {
            this.createAbsentTables = build.createAbsentTables.booleanValue();
        }
    }
}
