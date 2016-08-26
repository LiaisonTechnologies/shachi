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
