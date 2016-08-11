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
import com.liaison.shachi.resmgr.HBaseResourceManager;

import java.io.Closeable;
import java.io.IOException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public abstract class Managed<X> implements Closeable {

    private final HBaseResourceManager owner;
    private final HBaseContext context;
    private final X resource;
    
    private String strRep;
    
    public final HBaseResourceManager getOwner() {
        return this.owner;
    }
    public final HBaseContext getContext() {
        return this.context;
    }
    public final X getResource() {
        return this.resource;
    }
    public final X use() {
        return getResource();
    }
    
    @Override
    public abstract void close() throws IOException;
    
    /**
     * Return the output of {@link Object#hashCode()}.
     * <br><br>
     * There is no equivalence among instances of Managed; two Managed references are equal if and
     * only if they point to the same object. Therefore, {@link #hashCode()} merely returns the
     * result of {@link Object#hashCode()}. The latter method is only overridden here in order to
     * make it <code>final</code>, to prevent inheritors from changing this behavior.
     * @see {@link Object#hashCode()}
     * @return {@link Object#hashCode()}
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Check whether the provided reference also points to this instance. In other words, return
     * the output of {@link #equals(Object)}.
     * <br><br>
     * There is no equivalence among instances of Managed; two Managed references are equal if and
     * only if they point to the same object. Therefore, {@link #equals(Object)} merely returns the
     * result of {@link Object#equals(Object)}. The latter method is only overridden here in order
     * to make it <code>final</code>, to prevent inheritors from changing this behavior.
     * @see {@link #equals(Object)}
     * @return {@link #equals(Object)}
     */
    @Override
    public final boolean equals(final Object otherObj) {
        return super.equals(otherObj);
    }
    
    protected void addToStrRep(StringBuilder strGen) {
        // default no-op implementation
    }
    
    @Override
    public final String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(getClass().getSimpleName());
            strGen.append("(owner=");
            strGen.append(this.owner);
            strGen.append(",context.id=");
            strGen.append(this.context.getId());
            strGen.append(",res=");
            strGen.append(this.resource);
            strGen.append("|");
            addToStrRep(strGen);
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    public Managed(final HBaseResourceManager owner, final HBaseContext context, final X resource) {
        Util.ensureNotNull(owner, this, "owner", HBaseResourceManager.class);
        this.owner = owner;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        Util.ensureNotNull(resource, this, "resource");
        this.resource = resource;
        this.strRep = null;
    }
}
