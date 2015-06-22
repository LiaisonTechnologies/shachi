/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.request.fluid.ReadOpSpecFluid;
import com.liaison.hbase.api.request.fluid.WriteOpSpecFluid;

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