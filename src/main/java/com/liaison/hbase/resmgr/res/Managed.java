/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.resmgr.res;

import java.io.Closeable;
import java.io.IOException;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.resmgr.HBaseResourceManager;
import com.liaison.hbase.util.Util;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public abstract class Managed<X> implements Closeable {

    private final HBaseResourceManager owner;
    private final HBaseContext context;
    private final X resource;
    
    protected final HBaseResourceManager getOwner() {
        return this.owner;
    }
    protected final HBaseContext getContext() {
        return this.context;
    }
    protected final X getResource() {
        return this.resource;
    }
    
    public final X use() {
        return getResource();
    }
    
    @Override
    public abstract void close() throws IOException;
    
    public Managed(final HBaseResourceManager owner, final HBaseContext context, final X resource) {
        Util.ensureNotNull(owner, this, "owner", HBaseResourceManager.class);
        this.owner = owner;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        Util.ensureNotNull(resource, this, "resource");
        this.resource = resource;
    }
}
