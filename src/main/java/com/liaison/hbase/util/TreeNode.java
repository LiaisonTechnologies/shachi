/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
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
