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

import com.liaison.javabasics.commons.Util;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 12:52
 */
public class MiniClusterTestHBaseContext extends CommonHBaseContext {

    public static final class Builder extends AbstractHBaseContextBuilder<MiniClusterTestHBaseContext, Builder> {
        private HBaseTestingUtility hbTestUtil;

        public Builder hbTestUtil(final HBaseTestingUtility hbTestUtil) {
            this.hbTestUtil = hbTestUtil;
            super.configProvider(hbTestUtil::getConfiguration);
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }
        @Override
        public MiniClusterTestHBaseContext build() {
            return new MiniClusterTestHBaseContext(this);
        }
        private Builder() { }
    }

    private static final TableNamingStrategy
        DEFAULT_TABLENAMINGSTRATEGY = new IdentityTableNamingStrategy();

    public static final Builder getBuilder() {
        return new Builder();
    }

    private final HBaseTestingUtility hbTestUtil;

    @Override
    protected final TableNamingStrategy getDefaultTableNamingStrategy() {
        return DEFAULT_TABLENAMINGSTRATEGY;
    }

    @Override
    public HBaseAdmin buildAdmin() throws IOException {
        return hbTestUtil.getHBaseAdmin();
    }

    public MiniClusterTestHBaseContext(final Builder build) {
        super(build);
        Util.ensureNotNull(build.hbTestUtil, this, "hbTestUtil", HBaseTestingUtility.class);
        this.hbTestUtil = build.hbTestUtil;
    }
}
