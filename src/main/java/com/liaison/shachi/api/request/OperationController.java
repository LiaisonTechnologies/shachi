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
package com.liaison.shachi.api.request;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.request.fluid.ReadOpSpecFluid;
import com.liaison.shachi.api.request.fluid.WriteOpSpecFluid;

/**
 * Starting point during the HBase operation-spec generating process for individual read and write
 * operations, as well as the transition point when the chain of operations encapsulated in the
 * spec is to be executed to return a result.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <X> The type of the result to be returned when the operation specification is executed.
 */
public interface OperationController<X> extends OperationExecutor<X> {
    /**
     * Initiate a READ operation using the given handle.
     * @param handle the ID used to identify the operation
     * @return chaining/fluent API for specifying an HBase READ operation
     * @throws IllegalStateException if the controller is no longer accepting new operation
     * specifications to be executed
     * @throws IllegalArgumentException if handle is null
     */
    ReadOpSpecFluid<X> read(Object handle) throws IllegalStateException, IllegalArgumentException;
    /**
     * Initiate a WRITE operation using the given handle.
     * @param handle the ID used to identify the operation
     * @return chaining/fluent API for specifying an HBase WRITE operation
     * @throws IllegalStateException if the controller is no longer accepting new operation
     * specifications to be executed
     * @throws IllegalArgumentException if handle is null
     */
    WriteOpSpecFluid<X> write(Object handle) throws IllegalStateException, IllegalArgumentException;
    /**
     * Transfer control to an {@link OperationExecutor} identical to this one, except that the
     * {@link OperationExecutor#exec()} operation is executed asynchronously in a thread pool
     * managed by the {@link HBaseControl} instance which created this {@link OperationController},
     * and the {@link #read(Object)} and {@link #write(Object)} methods which allow additional
     * read/write operations to be added to this {@link OperationController} instance are no longer
     * available.
     * <br><br>
     * In particular, any HBase operation specifications which have <em>already been specified</em>
     * in this {@link OperationController} instance are carried over to the new, asynchronicity-
     * supporting {@link OperationExecutor} proxy, and will be carried out (asynchronously) upon
     * invoking {@link OperationExecutor#exec()} on that instance.
     * @return an asynchronous {@link OperationExecutor} proxy for this controller, using a thread
     * pool established by the original {@link HBaseControl} (if available).
     */
    OperationExecutor<ListenableFuture<X>> async();
}