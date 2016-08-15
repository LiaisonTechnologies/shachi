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
