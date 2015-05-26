/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

/**
 * Loosely represents a node in a directonal, n-ary tree, where each node points only to its sole
 * parent node. In practice, the representation does not store the parent node itself; only the
 * depth in the tree is stored. Upon instantiation, the depth is assigned to zero (for the root
 * node, where parent is null) or to the parent's depth plus one.
 * 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <A> Self-referential generic type representing the specific implementation to be returned
 * by {@link #self()}. Maintained to carry on the type information from {@link AbstractSelfRef}.
 */
public abstract class TreeNode<A extends TreeNode<A>> extends AbstractSelfRef<A> {

    private final int depth;
    
    /**
     * Get the depth of the current node in the tree.
     * @return the depth of the current node in the tree; if the node is the root, returns 0
     */
    protected final int getDepth() {
        return this.depth;
    }
    
    /**
     * Instantiate a TreeNode with the given parent. If the parent is null, set depth to zero;
     * otherwise, set the depth to the parent's depth plus one.
     * @param parent
     */
    public TreeNode(final TreeNode<?> parent) {
        if (parent == null) {
            this.depth = 0;
        } else {
            this.depth = parent.getDepth() + 1;
        }
    }
}
