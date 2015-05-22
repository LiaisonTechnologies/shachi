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
