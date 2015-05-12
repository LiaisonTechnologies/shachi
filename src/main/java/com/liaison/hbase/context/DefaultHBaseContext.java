package com.liaison.hbase.context;

import org.apache.hadoop.conf.Configuration;

import com.liaison.hbase.util.DefensiveCopyStrategy;

public class DefaultHBaseContext implements HBaseContext {

    public static final class Builder {
        private DefensiveCopyStrategy defensiveCopyStrategy;
        private Boolean createAbsentTables;
        public Builder createAbsentTables(final boolean createAbsentTables) {
            this.createAbsentTables = Boolean.valueOf(createAbsentTables);
            return this;
        }
        public Builder defensiveCopyStrategy(final DefensiveCopyStrategy defensiveCopyStrategy) {
            this.defensiveCopyStrategy = defensiveCopyStrategy;
            return this;
        }
        public DefaultHBaseContext build() {
            return new DefaultHBaseContext(this);
        }
        private Builder() {
            this.defensiveCopyStrategy = null;
            this.createAbsentTables = null;
        }
    }
    
    public static final DefensiveCopyStrategy DEFAULT_DEFENSIVE_COPY_STRATEGY = 
        DefensiveCopyStrategy.ALWAYS;
    public static final boolean DEFAULT_CREATE_ABSENT_TABLES = true;

    public static final Builder getBuilder() {
        return new Builder();
    }
    
    private final DefensiveCopyStrategy defensiveCopyStrategy;
    private final boolean createAbsentTables;
    
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
        // TODO figure out how to implement this
        return null;
    }
    
    private DefaultHBaseContext(final Builder build) {
        if (build.defensiveCopyStrategy == null) {
            this.defensiveCopyStrategy = DEFAULT_DEFENSIVE_COPY_STRATEGY;
        } else {
            this.defensiveCopyStrategy = build.defensiveCopyStrategy;
        }
        
        if (build.createAbsentTables == null) {
            this.createAbsentTables = DEFAULT_CREATE_ABSENT_TABLES;
        } else {
            this.createAbsentTables = build.createAbsentTables.booleanValue();
        }
    }
}
