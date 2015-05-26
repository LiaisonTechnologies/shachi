/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.context;

public class DefaultHBaseContext extends CommonHBaseContext {

    public static final class Builder extends AbstractHBaseContextBuilder<DefaultHBaseContext, Builder> {
        @Override
        public Builder self() {
            return this;
        }
        @Override
        public DefaultHBaseContext build() {
            return new DefaultHBaseContext(this);
        }
        private Builder() { }
    }
    
    private static final TableNamingStrategy
        DEFAULT_TABLENAMINGSTRATEGY = new IdentityTableNamingStrategy();

    public static final Builder getBuilder() {
        return new Builder();
    }
    
    @Override
    protected final TableNamingStrategy getDefaultTableNamingStrategy() {
        return DEFAULT_TABLENAMINGSTRATEGY;
    }
    
    public DefaultHBaseContext(final Builder build) {
        super(build);
    }
}
