package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.LinkedList;

import com.liaison.hbase.util.TreeNode;
import com.liaison.hbase.util.TreeNodeNonRoot;
import com.liaison.hbase.util.Util;

public abstract class StatefulSpec<A extends StatefulSpec<A, P>, P extends TreeNode<P>> extends TreeNodeNonRoot<A, P> implements Serializable {
    
    private static final long serialVersionUID = -6331552111315785761L;

    private static enum SpecState {
        FLUID, FROZEN;
    }

    private SpecState state;
    private final LinkedList<StatefulSpec<?,?>> subordSpecList;
    private String strRep;
    
    public final boolean isFrozen() {
        return this.state == SpecState.FROZEN;
    }
    protected final void prepMutation() throws IllegalStateException {
        if (isFrozen()) {
            throw new IllegalStateException("Cannot mutate post-freeze: " + toString());
        }
        this.strRep = null;
    }
    
    public final void freezeRecursive() {
        final LinkedList<StatefulSpec<?, ?>> subordSpecQueue;
        StatefulSpec<?, ?> currentSpec;
        
        subordSpecQueue = new LinkedList<>();
        subordSpecQueue.add(this);
        while (!subordSpecQueue.isEmpty()) {
            currentSpec = subordSpecQueue.removeFirst();
            if (!currentSpec.isFrozen()) {
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

    public StatefulSpec(final P parent) throws IllegalArgumentException {
        super(parent);
        this.state = SpecState.FLUID;
        this.subordSpecList = new LinkedList<>();
    }
}
