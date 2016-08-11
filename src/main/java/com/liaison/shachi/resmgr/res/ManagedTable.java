/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
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
