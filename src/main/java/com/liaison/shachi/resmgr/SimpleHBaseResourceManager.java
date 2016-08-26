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
