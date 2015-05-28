/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr;

import org.apache.hadoop.hbase.client.HTable;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseResourceAcquisitionException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.resmgr.res.ManagedAdmin;
import com.liaison.hbase.resmgr.res.ManagedTable;
import com.liaison.hbase.util.HBaseUtil;
import com.liaison.hbase.util.LogMeMaybe;
import com.liaison.hbase.util.Util;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ResourceManagerUtil {

    private static final LogMeMaybe LOG;
    
    public static ManagedTable borrow(final HBaseResourceManager sourceResMgr, final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        String logMsg;
        final HTable table;
        
        Util.ensureNotNull(context, sourceResMgr, "context", HBaseContext.class);
        Util.ensureNotNull(model, sourceResMgr, "model", TableModel.class);
        
        logMethodName =
            LOG.enter(()->"borrow(context.id=",
                      ()->context.getId(),
                      ()->",model=",
                      ()->model,
                      ()->")");

        try (ManagedAdmin admin = sourceResMgr.borrowAdmin(context)) {
            table = HBaseUtil.connectToTable(context, admin.use(), model);
            return new ManagedTable(sourceResMgr, context, model, table);
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
    
    static {
        LOG = new LogMeMaybe(ResourceManagerUtil.class);
    }
    
    private ResourceManagerUtil() { }
}
