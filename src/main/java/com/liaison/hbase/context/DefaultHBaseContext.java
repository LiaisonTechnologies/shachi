package com.liaison.hbase.context;

import org.apache.hadoop.conf.Configuration;

import com.liaison.hbase.util.DefensiveCopyStrategy;

public class DefaultHBaseContext implements HBaseContext {

    public static final class Builder {
        private DefensiveCopyStrategy defensiveCopyStrategy;
        public Builder defensiveCopyStrategy(final DefensiveCopyStrategy defensiveCopyStrategy) {
            this.defensiveCopyStrategy = defensiveCopyStrategy;
            return this;
        }
        public DefaultHBaseContext build() {
            return new DefaultHBaseContext(this);
        }
        private Builder() {
            this.defensiveCopyStrategy = null;
        }
    }
    
    public static final DefensiveCopyStrategy DEFAULT_DEFENSIVE_COPY_STRATEGY = 
        DefensiveCopyStrategy.ALWAYS;

    public static final Builder getBuilder() {
        return new Builder();
    }
    
    private final DefensiveCopyStrategy defensiveCopyStrategy;
    
    @Override
    public DefensiveCopyStrategy getDefensiveCopyStrategy() {
        return this.defensiveCopyStrategy;
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
    }
}
