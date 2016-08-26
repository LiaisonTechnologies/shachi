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

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.HBaseResourceReleaseException;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.resmgr.HBaseResourceManager;
import org.apache.hadoop.hbase.client.HTable;

import java.io.IOException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ManagedTable extends Managed<HTable> {
    
    private final TableModel model;

    public TableModel getModel() {
        return this.model;
    }
    
    @Override
    public void close() throws IOException {
        try {
            getOwner().release(this);
        } catch (HBaseResourceReleaseException exc) {
            // wrap in an IOException because Closeable requires it
            throw new IOException(exc);
        }
    }
    
    @Override
    protected void addToStrRep(final StringBuilder strGen) {
        strGen.append("model=");
        strGen.append(this.model);
    }

    public ManagedTable(final HBaseResourceManager owner, final HBaseContext context, final TableModel model, final HTable table) {
        super(owner, context, table);
        Util.ensureNotNull(model, this, "model", TableModel.class);
        this.model = model;
    }
}
