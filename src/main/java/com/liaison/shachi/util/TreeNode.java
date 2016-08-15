/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.util;

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
