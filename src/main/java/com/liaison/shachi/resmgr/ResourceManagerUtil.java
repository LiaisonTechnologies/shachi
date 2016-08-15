/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.resmgr;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.HBaseResourceAcquisitionException;
import com.liaison.shachi.exception.HBaseResourceReleaseException;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.resmgr.res.Managed;
import com.liaison.shachi.resmgr.res.ManagedAdmin;
import com.liaison.shachi.resmgr.res.ManagedTable;
import com.liaison.shachi.util.HBaseUtil;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import java.io.Closeable;
import java.io.IOException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ResourceManagerUtil {

    private static final JitLog LOG;
    
    private static final String LOGNAME_BUILDTABLE = "buildTableResource";
    private static final String LOGNAME_FULL_BUILDTABLE =
        ResourceManagerUtil.class.getSimpleName() + LOGNAME_BUILDTABLE;
    private static final String LOGNAME_BUILDADMIN = "buildAdminResource";
    private static final String LOGNAME_FULL_BUILDADMIN =
        ResourceManagerUtil.class.getSimpleName() + LOGNAME_BUILDADMIN;
    private static final String LOGNAME_DESTROYTABLE = "destroyTableResource";
    private static final String LOGNAME_FULL_DESTROYTABLE =
        ResourceManagerUtil.class.getSimpleName() + LOGNAME_DESTROYTABLE;
    private static final String LOGNAME_DESTROYADMIN = "destroyAdminResource";
    private static final String LOGNAME_FULL_DESTROYADMIN =
        ResourceManagerUtil.class.getSimpleName() + LOGNAME_DESTROYADMIN;
    
    /**
     * TODO
     * @param sourceResMgr
     * @param context
     * @param model
     * @return
     * @throws HBaseResourceAcquisitionException
     * @throws IllegalArgumentException
     */
    public static ManagedTable buildTableResource(final HBaseResourceManager sourceResMgr, final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        String logMsg;
        final HTable table;
        
        Util.ensureNotNull(sourceResMgr,
                           LOGNAME_FULL_BUILDTABLE,
                           "sourceResMgr",
                           HBaseResourceManager.class);
        Util.ensureNotNull(context, LOGNAME_FULL_BUILDTABLE, "context", HBaseContext.class);
        Util.ensureNotNull(model, LOGNAME_FULL_BUILDTABLE, "model", TableModel.class);
        
        logMethodName =
            LOG.enter(()->LOGNAME_BUILDTABLE,
                      ()->"(context.id=",
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
    
    /**
     * TODO
     * @param sourceResMgr
     * @param context
     * @return
     * @throws HBaseResourceAcquisitionException
     * @throws IllegalArgumentException
     */
    public static ManagedAdmin buildAdminResource(final HBaseResourceManager sourceResMgr, final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        final HBaseAdmin admin;
        
        Util.ensureNotNull(sourceResMgr,
                           LOGNAME_FULL_BUILDADMIN,
                           "sourceResMgr",
                           HBaseResourceManager.class);
        Util.ensureNotNull(context, LOGNAME_FULL_BUILDADMIN, "context", HBaseContext.class);
        
        logMethodName =
            LOG.enter(()->LOGNAME_BUILDADMIN, ()->"(context.id=", ()->context.getId(), ()->")");
        
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
        return new ManagedAdmin(sourceResMgr, context, admin);
    }
    
    private static void destroy(final Managed<? extends Closeable> res, final String resName, final Class<? extends Managed<?>> resType, final String methodName, final String methodNameFull) throws HBaseResourceReleaseException, IllegalArgumentException {
        final String logMethodName;
        final String logMsg;
        
        Util.ensureNotNull(res, methodNameFull, resName, resType);
        logMethodName = LOG.enter(()->methodName, ()->"(", ()->res, ()->")");
        
        try {
            res.getResource().close();
        } catch (IOException exc) {
            logMsg = "Failed to destroy: "
                     + res
                     + "; "
                     + exc.toString();
            throw new HBaseResourceReleaseException(logMsg, exc);
        } finally {
            LOG.leave(logMethodName);
        }
    }
    
    /**
     * TODO
     * @param mTable
     * @throws HBaseResourceReleaseException
     * @throws IllegalArgumentException
     */
    public static void destroyTableResource(final ManagedTable mTable) throws HBaseResourceReleaseException, IllegalArgumentException {
        destroy(mTable,
                "mTable",
                ManagedTable.class,
                LOGNAME_DESTROYTABLE,
                LOGNAME_FULL_DESTROYTABLE);
    }
    
    /**
     * TODO
     * @param mAdmin
     * @throws HBaseResourceReleaseException
     * @throws IllegalArgumentException
     */
    public static void destroyAdminResource(final ManagedAdmin mAdmin) throws HBaseResourceReleaseException, IllegalArgumentException {
        destroy(mAdmin,
                "mAdmin",
                ManagedAdmin.class,
                LOGNAME_DESTROYADMIN,
                LOGNAME_FULL_DESTROYADMIN);
    }
    
    static {
        LOG = new JitLog(ResourceManagerUtil.class);
    }
    
    private ResourceManagerUtil() { }
}
