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
 * Specific case of {@link TreeNode} for root nodes, where the parent is null. Depth for TreeNodes
 * of type TreeNodeRoot is always zero.
 * 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <A> Self-referential generic type representing the specific implementation to be returned
 * by {@link #self()}. Maintained to carry on the type information from {@link AbstractSelfRef}.
 */
public abstract class TreeNodeRoot<A extends TreeNodeRoot<A>> extends TreeNode<A> {
    public TreeNodeRoot() {
        super(null);
    }
}
