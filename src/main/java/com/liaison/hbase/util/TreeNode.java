package com.liaison.hbase.util;

public abstract class TreeNode<A extends TreeNode<A>> extends AbstractSelfRef<A> {

    private final int depth;
    
    protected final int getDepth() {
        return this.depth;
    }
    
    public TreeNode(final TreeNode<?> parent) {
        if (parent == null) {
            this.depth = 0;
        } else {
            this.depth = parent.getDepth() + 1;
        }
    }
}
