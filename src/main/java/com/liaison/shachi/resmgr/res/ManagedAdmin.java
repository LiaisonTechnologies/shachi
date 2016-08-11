/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.resmgr.res;

import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.HBaseResourceReleaseException;
import com.liaison.shachi.resmgr.HBaseResourceManager;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ManagedAdmin extends Managed<HBaseAdmin> {

    @Override
    public void close() throws IOException {
        try {
            getOwner().releaseAdmin(this);
        } catch (HBaseResourceReleaseException exc) {
            // wrap in an IOException because Closeable requires it
            throw new IOException(exc);
        }
    }

    public ManagedAdmin(final HBaseResourceManager owner, final HBaseContext context, final HBaseAdmin admin) {
        super(owner, context, admin);
    }
}
