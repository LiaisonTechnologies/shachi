/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.StringRepFormat;
import com.liaison.hbase.util.TreeNode;
import com.liaison.hbase.util.TreeNodeNonRoot;
import com.liaison.hbase.util.Util;

public abstract class StatefulSpec<A extends StatefulSpec<A, P>, P extends TreeNode<P>> extends TreeNodeNonRoot<A, P> implements Serializable {
    
    private static final long serialVersionUID = -6331552111315785761L;

    private SpecState state;
    private final LinkedList<StatefulSpec<?,?>> subordSpecList;
    private final ConcurrentHashMap<StringRepFormat, String> strRep;
    private Integer hc;
    
    public final SpecState getState() {
        return this.state;
    }
    public final boolean isFrozen() {
        return this.state == SpecState.FROZEN;
    }
    protected final void prepMutation() throws IllegalStateException {
        if (isFrozen()) {
            throw new IllegalStateException("Cannot mutate post-freeze: " + toString());
        }
        /*
         * reset generated string representation and int value, as the core properties from which
         * they were generated are changing
         */
        this.strRep.clear();
        this.hc = null;
    }
    
    protected void validate() throws SpecValidationException {
        // provide a default implementation which does nothing
        // TODO implement this in inheritors, where relevant
    }
    
    public final void freezeRecursive() throws SpecValidationException {
        final LinkedList<StatefulSpec<?, ?>> subordSpecQueue;
        StatefulSpec<?, ?> currentSpec;
        
        subordSpecQueue = new LinkedList<>();
        subordSpecQueue.add(this);
        while (!subordSpecQueue.isEmpty()) {
            currentSpec = subordSpecQueue.removeFirst();
            if (!currentSpec.isFrozen()) {
                currentSpec.validate();
                currentSpec.state = SpecState.FROZEN;
                subordSpecQueue.addAll(currentSpec.subordSpecList);
            }
        }
    }
    
    protected final void addSubordinate(final StatefulSpec<?,?> subordSpec) throws IllegalArgumentException {
        Util.ensureNotNull(subordSpec, this, "subordSpec", StatefulSpec.class);
        this.subordSpecList.add(subordSpec);
    }
    
    protected abstract String prepareStrRepHeadline();
    
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        // provide a default implementation which does nothing
    }
    
    public final String toString(final StringRepFormat format) {
        final StringBuilder strGen;
        String returnString;
        
        returnString = this.strRep.get(format);
        if (returnString == null) {
            strGen = new StringBuilder();
            if (format == StringRepFormat.STRUCTURED) {
                Util.appendIndented(strGen, getDepth(), prepareStrRepHeadline(), "\n");
            } else if (format == StringRepFormat.INLINE) {
                Util.append(strGen, prepareStrRepHeadline());
            }
            prepareStrRep(strGen, format);
            returnString = strGen.toString();
            this.strRep.putIfAbsent(format, returnString);
        }
        return returnString;
    }
    
    @Override
    public final String toString() {
        return toString(StringRepFormat.INLINE);
    }
    
    protected abstract int prepareHashCode();
    
    @Override
    public final int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(prepareHashCode());
        }
        return this.hc.intValue();
    }
    
    @Override
    public abstract boolean equals(final Object otherObj);

    public StatefulSpec(final P parent) throws IllegalArgumentException {
        super(parent);
        this.state = SpecState.FLUID;
        this.subordSpecList = new LinkedList<>();
        this.strRep = new ConcurrentHashMap<>();
    }
}
