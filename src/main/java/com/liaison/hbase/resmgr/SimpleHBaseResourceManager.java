/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.exception.HBaseResourceReleaseException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.resmgr.res.ManagedAdmin;
import com.liaison.hbase.resmgr.res.ManagedTable;
import com.liaison.hbase.util.HBaseUtil;
import com.liaison.hbase.util.LogMeMaybe;
import com.liaison.hbase.util.Util;

public enum SimpleHBaseResourceManager implements HBaseResourceManager {
    INSTANCE;
    
    private static LogMeMaybe LOG = new LogMeMaybe(SimpleHBaseResourceManager.class);

    @Override
    public ManagedTable borrow(final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        String logMsg;
        final HTable table;
        
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        Util.ensureNotNull(model, this, "model", TableModel.class);
        
        logMethodName =
            LOG.enter(()->"borrow(context.id=",
                      ()->context.getId(),
                      ()->",model=",
                      ()->model,
                      ()->")");

        try (ManagedAdmin admin = borrowAdmin(context)) {
            table = HBaseUtil.connectToTable(context, admin.use(), model);
            return new ManagedTable(this, context, model, table);
        } catch (Exception exc) {
            logMsg = "Failed to connect to table "
                     + model
                     + " for context with ID '"
                     + context.getId()
                     + "'; "
                     + exc.toString();
            LOG.error(logMsg, exc);
            throw new HBaseResourceAcquisitionException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }

    @Override
    public void release(final HBaseContext context, final TableModel model, final HTable table) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        Util.ensureNotNull(model, this, "model", TableModel.class);
        Util.ensureNotNull(model, this, "table", HTable.class);
        
        logMethodName =
            LOG.enter(()->"release(context.id=",
                      ()->context.getId(),
                      ()->",model=",
                      ()->model,
                      ()->")");
        
        try {
            table.close();
        } catch (IOException exc) {
            logMsg = "Failed to release and close table resource ("
                     + model
                     + ") for context with ID '"
                     + context.getId()
                     + "'; "
                     + exc.toString();
            throw new HBaseResourceReleaseException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }

    @Override
    public ManagedAdmin borrowAdmin(final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final HBaseAdmin admin;
        
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        
        logMethodName =
            LOG.enter(()->"releaseAdmin(context.id=", ()->context.getId(), ()->")");
        
        try {
            admin = HBaseUtil.connectAdmin(context);
        } catch (Exception exc) {
            logMsg = "Failed to connect to admin resource for context with ID '"
                     + context.getId()
                     + "'; "
                     + exc.toString();
            LOG.error(logMsg, exc);
            throw new HBaseResourceAcquisitionException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
        return new ManagedAdmin(this, context, admin);
    }

    @Override
    public void releaseAdmin(final HBaseContext context, final HBaseAdmin admin) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        
        logMethodName =
            LOG.enter(()->"releaseAdmin(context.id=", ()->context.getId(), ()->")");
        
        try {
            admin.close();
        } catch (IOException exc) {
            logMsg = "Failed to release and close admin resource for context with ID '"
                     + context.getId()
                     + "'; "
                     + exc.toString();
            throw new HBaseResourceReleaseException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }
}
