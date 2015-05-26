/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.context;


public class MapRHBaseContext extends CommonHBaseContext {

    public static final class Builder extends AbstractHBaseContextBuilder<MapRHBaseContext, Builder> {
        @Override
        public Builder self() {
            return this;
        }
        @Override
        public MapRHBaseContext build() {
            return new MapRHBaseContext(this);
        }
        private Builder() { }
    }
    
    private static final String DEFAULT_MAPRTABLES_PREFIX = "/user/mapr/tables/";
    private static final TableNamingStrategy
        DEFAULT_TABLENAMINGSTRATEGY =
            new DirectoryPrefixedTableNamingStrategy(DEFAULT_MAPRTABLES_PREFIX);

    public static final Builder getBuilder() {
        return new Builder();
    }
    
    @Override
    protected final TableNamingStrategy getDefaultTableNamingStrategy() {
        return DEFAULT_TABLENAMINGSTRATEGY;
    }
    
    public MapRHBaseContext(final Builder build) {
        super(build);
    }
}
