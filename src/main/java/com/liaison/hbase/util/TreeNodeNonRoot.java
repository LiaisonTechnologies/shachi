/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;


import com.liaison.commons.Util;

/**
 * Extension of {@link TreeNode} for cases where the node is known not to be a root node, and
 * therefore to have a non-null parent. Also maintains a final reference to the parent node, which
 * is of type {@link TreeNode}, and may or may not be of the same type as the node itself.
 * 
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <A> Self-referential generic type representing the specific implementation to be returned
 * by {@link #self()}. Maintained to carry on the type information from {@link AbstractSelfRef}.
 * @param <P> Subtype of {@link TreeNode} for the parent entity. Can be the same type as &lt;A&gt;,
 * but is not necessarily.
 */
public abstract class TreeNodeNonRoot<A extends TreeNode<A>, P extends TreeNode<?>> extends TreeNode<A> {
    
    private final P parent;
    
    /**
     * Get the parent {@link TreeNode}; gauranteed to be non-null.
     * @return the parent {@link TreeNode} (non-null)
     */
    public P getParent() {
        return this.parent;
    }
    
    /**
     * Constructor
     * @param parent the parent {@link TreeNode}; must be non-null
     * @throws IllegalArgumentException if parent is null
     */
    public TreeNodeNonRoot(final P parent) throws IllegalArgumentException {
        super(parent);
        Util.ensureNotNull(parent, this, "parent");
        this.parent = parent;
    }
}
