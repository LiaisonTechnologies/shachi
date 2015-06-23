/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.StringRepFormat;
import com.liaison.hbase.util.TreeNode;
import com.liaison.hbase.util.TreeNodeNonRoot;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An element in an HBase operation specification tree which can be in one of two states:
 * <ul>
 * <li><strong>{@link SpecState#FLUID}</strong>: The specification node is still in a "generation"
 * phase, wherein the client may execute operations </li>
 * </ul>
 * 
 * TODO
 * 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <A>
 * @param <P>
 */
public abstract class StatefulSpec<A extends StatefulSpec<A, P>, P extends TreeNode<P>> extends TreeNodeNonRoot<A, P> implements Serializable {
    
    private static final long serialVersionUID = -6331552111315785761L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private SpecState state;
    private final LinkedList<StatefulSpec<?,?>> subordSpecList;
    private final ConcurrentHashMap<StringRepFormat, String> strRep;
    private Integer hc;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS                                                                    ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * TODO
     * @return
     */
    public final SpecState getState() {
        return this.state;
    }
    
    /**
     * TODO
     * @return
     */
    public final boolean isFrozen() {
        return this.state == SpecState.FROZEN;
    }
    
    /**
     * TODO
     * @throws IllegalStateException
     */
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

    /**
     * Enforces that the given operation may <strong>only</strong> be executed <em>after</em> the
     * corresponding spec has transitioned from FLUID to FROZEN state.
     * @param operationName name of the operation to be executed; will be included in the
     * Exception message if an IllegalStateException is thrown due to the spec not being in frozen
     * state.
     * @throws IllegalStateException if the spec is not in frozen state
     */
    protected final void prepPostFreezeOp(final Object operationName) throws IllegalStateException {
        if (!isFrozen()) {
            throw new IllegalStateException("Operation '"
                                            + String.valueOf(operationName)
                                            + "' not supported until spec is frozen: "
                                            + toString());
        }
    }
    
    /**
     * TODO
     * @throws SpecValidationException
     */
    protected void validate() throws SpecValidationException {
        // provide a default implementation which does nothing
        // TODO implement this in inheritors, where relevant
    }
    
    /**
     * TODO
     * @throws SpecValidationException
     */
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
    
    /**
     * TODO
     * @param subordSpec
     * @throws IllegalArgumentException
     */
    protected final void addSubordinate(final StatefulSpec<?,?> subordSpec) throws IllegalArgumentException {
        Util.ensureNotNull(subordSpec, this, "subordSpec", StatefulSpec.class);
        this.subordSpecList.add(subordSpec);
    }

    /**
     * TODO
     * @return
     */
    protected final List<StatefulSpec<?,?>> getSubordSpecList() {
        return Collections.unmodifiableList(this.subordSpecList);
    }
    
    /**
     * TODO
     * @return
     */
    protected abstract String prepareStrRepHeadline();
    
    /**
     * TODO
     * @param strGen
     * @param format
     */
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        // provide a default implementation which does nothing
    }
    
    /**
     * TODO
     * @param format
     * @return
     */
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
    
    /**
     * TODO
     */
    @Override
    public final String toString() {
        return toString(StringRepFormat.INLINE);
    }
    
    /**
     * TODO
     * @return
     */
    protected abstract int prepareHashCode();
    
    /**
     * TODO
     */
    @Override
    public final int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(prepareHashCode());
        }
        return this.hc.intValue();
    }
    
    /**
     * TODO
     */
    @Override
    public abstract boolean equals(final Object otherObj);
    
    // ||----(instance methods)------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    /**
     * TODO
     * @param parent
     * @throws IllegalArgumentException
     */
    public StatefulSpec(final P parent) throws IllegalArgumentException {
        super(parent);
        this.state = SpecState.FLUID;
        this.subordSpecList = new LinkedList<>();
        this.strRep = new ConcurrentHashMap<>();
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
