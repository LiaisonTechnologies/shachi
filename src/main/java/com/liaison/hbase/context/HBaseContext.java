package com.liaison.hbase.context;

import org.apache.hadoop.conf.Configuration;

public interface HBaseContext {
    Configuration getHBaseConfiguration();
    DefensiveCopyStrategy getDefensiveCopyStrategy();
}
