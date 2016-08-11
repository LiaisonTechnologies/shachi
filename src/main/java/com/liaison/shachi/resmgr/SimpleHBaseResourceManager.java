/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.resmgr;

import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.HBaseResourceAcquisitionException;
import com.liaison.shachi.exception.HBaseResourceReleaseException;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.resmgr.res.ManagedAdmin;
import com.liaison.shachi.resmgr.res.ManagedTable;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public enum SimpleHBaseResourceManager implements HBaseResourceManager {
    INSTANCE;

    @Override
    public ManagedTable borrow(final HBaseContext context, final TableModel model) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        return ResourceManagerUtil.buildTableResource(this, context, model);
    }
    @Override
    public void release(final ManagedTable mTable) throws HBaseResourceReleaseException, IllegalArgumentException {
        ResourceManagerUtil.destroyTableResource(mTable);
    }
    @Override
    public ManagedAdmin borrowAdmin(final HBaseContext context) throws HBaseResourceAcquisitionException, IllegalArgumentException {
        return ResourceManagerUtil.buildAdminResource(this, context);
    }
    @Override
    public void releaseAdmin(final ManagedAdmin mAdmin) throws HBaseResourceReleaseException, IllegalArgumentException {
        ResourceManagerUtil.destroyAdminResource(mAdmin);
    }
}
