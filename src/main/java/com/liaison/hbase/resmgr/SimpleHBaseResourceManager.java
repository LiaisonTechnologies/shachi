package com.liaison.hbase.resmgr;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.exception.HBaseResourceReleaseException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.HBaseUtil;
import com.liaison.hbase.util.LogMeMaybe;
import com.liaison.hbase.util.Util;

public enum SimpleHBaseResourceManager implements HBaseResourceManager {
    INSTANCE;
    
    private static LogMeMaybe LOG;

    @Override
    public HTable borrow(final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        String logMsg;
        HBaseAdmin admin = null;
        
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        Util.ensureNotNull(model, this, "model", TableModel.class);
        
        logMethodName =
            LOG.enter(()->"borrow(context.id=", ()->context.getId(), ()->",model=", ()->model);

        // ugh, this is so, so ugly... :(
        try {
            admin = borrowAdmin(context);
            return HBaseUtil.connectToTable(context, admin, model);
        } catch (Exception exc) {
            logMsg = "Failed to connect to table " + model + "; " + exc.toString();
            LOG.error(logMsg, exc);
            throw new HBaseResourceAcquisitionException(logMsg, exc);
        } finally {
            if (admin != null) {
                try {
                    releaseAdmin(context, admin);
                } catch (HBaseResourceReleaseException exc) {
                    logMsg = "RESOURCE LEAK: Failed to close "
                             + HBaseAdmin.class
                             + " object acquired in order to connect (context="
                             + context
                             + ",model="
                             + model
                             + "); "
                             + exc.toString();
                    LOG.error(logMethodName, logMsg, exc);
                }
            }
            LOG.leave(logMethodName);
        }
    }

    @Override
    public void release(final HBaseContext context, final TableModel model, final HTable table) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMsg;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        Util.ensureNotNull(model, this, "model", TableModel.class);
        Util.ensureNotNull(model, this, "table", HTable.class);
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
        }
    }

    @Override
    public HBaseAdmin borrowAdmin(final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        return HBaseUtil.connectAdmin(context);
    }

    @Override
    public void releaseAdmin(final HBaseContext context, final HBaseAdmin admin) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMsg;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        try {
            admin.close();
        } catch (IOException exc) {
            logMsg = "Failed to release and close admin resource for context with ID '"
                     + context.getId()
                     + "'; "
                     + exc.toString();
            throw new HBaseResourceReleaseException(logMsg, exc);
        }
    }
}
