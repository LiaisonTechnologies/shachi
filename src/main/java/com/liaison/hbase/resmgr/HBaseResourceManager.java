package com.liaison.hbase.resmgr;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.exception.HBaseResourceReleaseException;
import com.liaison.hbase.model.TableModel;

public interface HBaseResourceManager {
    HBaseAdmin borrowAdmin(HBaseContext context) throws HBaseResourceAcquisitionException;
    void releaseAdmin(HBaseContext context, HBaseAdmin admin) throws HBaseResourceReleaseException;
    HTable borrow(HBaseContext context, TableModel model) throws HBaseResourceAcquisitionException;
    void release(HBaseContext context, TableModel model, HTable table) throws HBaseResourceReleaseException;
}
