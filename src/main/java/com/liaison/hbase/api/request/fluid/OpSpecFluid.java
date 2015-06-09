/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import com.liaison.hbase.api.request.OperationController;

/**
 * Operation specification (either read or write) with a parent {@link OperationController}.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z> The operation result type which will be produced when the owning
 * {@link OperationController} executes {@link OperationController#exec()}.
 */
public interface OpSpecFluid<Z> {
    /**
     * Freeze a (read or write) operation specification and transfer control back to the owning
     * {@link OperationController}, such that the controller may either add an additional operation
     * to the chain, or execute the existing chain of operations. Implementations must ensure that
     * invoking this method transitions the <em>state</em> associated with the operation from
     * {@link SpecState#FLUID} to {@link SpecState#FROZEN}, guaranteeing that the API will return
     * subsequently return spec instances typed with the <code>~Frozen</code> interfaces for
     * already-specified operations (rather than with the <code>~Fluid</code> interfaces), and that
     * attempts to invoke operations specified in the <code>~Fluid</code> interfaces will throw
     * IllegalStateException.
     * @return the {@link OperationController} instance which owns this operation
     */
    OperationController<Z> then();
}
