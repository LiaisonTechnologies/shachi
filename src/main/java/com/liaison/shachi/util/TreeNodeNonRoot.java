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


import com.liaison.javabasics.commons.Util;

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
