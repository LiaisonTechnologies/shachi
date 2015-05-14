package com.liaison.hbase.context;

import org.apache.hadoop.conf.Configuration;

import com.liaison.hbase.util.DefensiveCopyStrategy;

public interface HBaseContext {
    Configuration getHBaseConfiguration();
    DefensiveCopyStrategy getDefensiveCopyStrategy();
    TableNamingStrategy getTableNamingStrategy();
    boolean doCreateAbsentTables();
}
