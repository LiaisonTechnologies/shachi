/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.exception.HBaseResourceReleaseException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.resmgr.res.ManagedAdmin;
import com.liaison.hbase.resmgr.res.ManagedTable;

public interface HBaseResourceManager {
    ManagedAdmin borrowAdmin(HBaseContext context) throws HBaseResourceAcquisitionException;
    void releaseAdmin(HBaseContext context, HBaseAdmin admin) throws HBaseResourceReleaseException;
    ManagedTable borrow(HBaseContext context, TableModel model) throws HBaseResourceAcquisitionException;
    void release(HBaseContext context, TableModel model, HTable table) throws HBaseResourceReleaseException;
}
