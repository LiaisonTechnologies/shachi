package com.liaison.hbase.util;


public abstract class TreeNodeNonRoot<A extends TreeNode<A>, P extends TreeNode<?>> extends TreeNode<A> {
    
    private final P parent;
    
    public P getParent() {
        return this.parent;
    }
    
    public TreeNodeNonRoot(final P parent) throws IllegalArgumentException {
        super(parent);
        Util.ensureNotNull(parent, this, "parent");
        this.parent = parent;
    }
}
