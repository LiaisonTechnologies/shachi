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

public interface HBaseResourceManager {
    ManagedAdmin borrowAdmin(HBaseContext context) throws HBaseResourceAcquisitionException;
    void releaseAdmin(ManagedAdmin admin) throws HBaseResourceReleaseException;
    ManagedTable borrow(HBaseContext context, TableModel model) throws HBaseResourceAcquisitionException;
    void release(ManagedTable table) throws HBaseResourceReleaseException;
}
