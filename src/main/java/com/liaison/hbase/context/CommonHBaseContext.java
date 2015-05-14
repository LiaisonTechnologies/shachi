package com.liaison.hbase.context;

import java.util.function.Supplier;

import org.apache.hadoop.conf.Configuration;

import com.liaison.hbase.util.AbstractSelfRefBuilder;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.Util;

public abstract class CommonHBaseContext implements HBaseContext {

    protected abstract static class AbstractHBaseContextBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        protected DefensiveCopyStrategy defensiveCopyStrategy;
        protected Boolean createAbsentTables;
        protected TableNamingStrategy tableNamingStrategy;
        protected Supplier<Configuration> configProvider;
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
            this.defensiveCopyStrategy = null;
            this.createAbsentTables = null;
            this.tableNamingStrategy = null;
            this.configProvider = null;
        }
    }
    
    public static final DefensiveCopyStrategy DEFAULT_DEFENSIVE_COPY_STRATEGY = 
        DefensiveCopyStrategy.ALWAYS;
    public static final boolean DEFAULT_CREATE_ABSENT_TABLES = true;
    
    private final DefensiveCopyStrategy defensiveCopyStrategy;
    private final boolean createAbsentTables;
    private final TableNamingStrategy tableNamingStrategy;
    private final Supplier<Configuration> configProvider;
    
    protected abstract TableNamingStrategy getDefaultTableNamingStrategy();
    
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
        Util.ensureNotNull(build.configProvider, this, "configProvider", Supplier.class);
        this.configProvider = build.configProvider;
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
