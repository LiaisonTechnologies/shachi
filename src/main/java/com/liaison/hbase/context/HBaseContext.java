package com.liaison.hbase.context;

import org.apache.hadoop.conf.Configuration;

import com.liaison.hbase.resmgr.ResourceConnectTolerance;
import com.liaison.hbase.util.DefensiveCopyStrategy;

public interface HBaseContext {
    Object getId();
    ResourceConnectTolerance getResourceConnectTolerance();
    Configuration getHBaseConfiguration();
    DefensiveCopyStrategy getDefensiveCopyStrategy();
    TableNamingStrategy getTableNamingStrategy();
    boolean doCreateAbsentTables();
}
