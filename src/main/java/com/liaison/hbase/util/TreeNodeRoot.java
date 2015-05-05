package com.liaison.hbase.util;

public abstract class TreeNodeRoot<A extends TreeNodeRoot<A>> extends TreeNode<A> {
    public TreeNodeRoot() {
        super(null);
    }
}
