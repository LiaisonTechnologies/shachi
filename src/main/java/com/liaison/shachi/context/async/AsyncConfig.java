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
package com.liaison.shachi.context.async;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface AsyncConfig {
    /**
     * Indicates whether this configuration supports asynchronous operations, and consequently
     * whether the HBaseControl which owns it maintains a thread pool for the purpose of executing
     * such operations. Other configurations pertaining to pool size are ignored if this
     * configuration does not enable asynchronicity.
     * @return true if asynchronous execution is supported within a thread pool maintained by the
     * owning HBaseControl instance; false otherwise
     */
    boolean isAsyncEnabled();
    /**
     * The minimum size of the thread pool in which asynchronous operations will be executed. Must
     * return one of the following:
     * <ul>
     * <li><strong>0</strong>: Configures a thread pool which need not maintain any idle threads.
     * </li>
     * <li><strong>positive integer</strong>: Configures a thread pool which must maintain at least
     * this total number of threads (whether idle or active).</li>
     * <li><strong>null</strong>: Equivalent to <strong>0</strong>.</li>
     * </ul>
     * If {@link #isAsyncEnabled()} returns true, the value returned by this method must be
     * lesser-than-or-equal to the value produced by {@link #getAsyncPoolMaxSize()}.
     * @return Integer indicating minimum asynchronous thread pool size; note that this value
     * <strong>may be null</strong>, per the rules indicated above (null is equivalent to zero).
     */
    Integer getAsyncPoolMinSize();
    /**
     * The maximum size of the thread pool in which asynchronous operations will be executed. Must
     * return one of the following:
     * <ul>
     * <li><strong>0</strong>: Configures an empty thread pool. Zero <em>must <strong>not</strong>
     * be returned</em> if {@link #isAsyncEnabled()} returns true.</li>
     * <li><strong>positive integer</strong>: Configures a thread pool which may not expand beyond
     * this number of threads (whether idle or active).</li>
     * <li><strong>null</strong>: Configures a thread pool of unlimited size (effectively
     * equivalent to Integer.MAX_VALUE).</li>
     * </ul>
     * If {@link #isAsyncEnabled()} returns true, the value returned by this method must be
     * greater-than-or-equal to the value produced by {@link #getAsyncPoolMinSize()}.
     * @return Integer indicating maximum asynchronous thread pool size; note that this value
     * <strong>may be null</strong>, per the rules indicated above (null is equivalent to zero).
     */
    Integer getAsyncPoolMaxSize();
    int getMinSizeForThreadPool();
    int getMaxSizeForThreadPool();
}
