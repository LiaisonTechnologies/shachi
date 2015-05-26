/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
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
