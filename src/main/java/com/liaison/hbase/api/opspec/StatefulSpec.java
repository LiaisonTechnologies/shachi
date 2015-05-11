package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.LinkedList;

import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.TreeNode;
import com.liaison.hbase.util.TreeNodeNonRoot;
import com.liaison.hbase.util.Util;

public abstract class StatefulSpec<A extends StatefulSpec<A, P>, P extends TreeNode<P>> extends TreeNodeNonRoot<A, P> implements Serializable {
    
    private static final long serialVersionUID = -6331552111315785761L;

    private SpecState state;
    private final LinkedList<StatefulSpec<?,?>> subordSpecList;
    private String strRep;
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
        this.strRep = null;
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
    protected void prepareStrRep(final StringBuilder strGen) {
        // provide a default implementation which does nothing
    }
    @Override
    public final String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            Util.appendIndented(strGen, getDepth(), prepareStrRepHeadline(), "\n");
            prepareStrRep(strGen);
            this.strRep = strGen.toString();
        }
        return this.strRep;
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
    }
}
