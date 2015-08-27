package com.liaison.hbase.context;

import com.liaison.commons.Util;
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
