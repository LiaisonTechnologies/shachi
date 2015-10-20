/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.context;

import com.liaison.hbase.context.async.AsyncConfig;
import com.liaison.hbase.resmgr.ResourceConnectTolerance;
import com.liaison.serialization.DefensiveCopyStrategy;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

public interface HBaseContext {
    Object getId();
    AsyncConfig getAsyncConfig();    
    ResourceConnectTolerance getResourceConnectTolerance();
    Configuration getHBaseConfiguration();
    DefensiveCopyStrategy getDefensiveCopyStrategy();
    TableNamingStrategy getTableNamingStrategy();
    boolean doCreateAbsentTables();
    HBaseAdmin buildAdmin()  throws IOException;
}
