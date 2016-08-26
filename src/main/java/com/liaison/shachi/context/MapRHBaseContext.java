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
package com.liaison.shachi.context;


public class MapRHBaseContext extends ConfigurationDrivenHBaseContext {

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
