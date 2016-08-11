package com.liaison.shachi.context;

import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 12:43
 */
public abstract class ConfigurationDrivenHBaseContext extends CommonHBaseContext {

    public HBaseAdmin buildAdmin() throws IOException {
        return new HBaseAdmin(getHBaseConfiguration());
    }
    protected ConfigurationDrivenHBaseContext(AbstractHBaseContextBuilder<?, ?> build) {
        super(build);
    }
}
