/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.fluid;

import com.liaison.shachi.api.request.OperationController;
import com.liaison.shachi.api.request.impl.SpecState;
import com.liaison.shachi.exception.SpecValidationException;

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
     * <br /><br />
     * Concurrent with the state transition from {@link SpecState#FLUID} to
     * {@link SpecState#FROZEN}, validation is performed on the operation spec to ensure that it
     * meets the requirements of the underlying HBase operation which will be created in order to
     * execute it.
     * @throws SpecValidationException if the validation step incident to the state transition
     * ({@link SpecState#FLUID} to {@link SpecState#FROZEN}) fails.
     * @return the {@link OperationController} instance which owns this operation
     */
    OperationController<Z> then() throws SpecValidationException;

    /**
     * Identical in all respects to {@link #then()}; exists only to provide naming which is
     * argubly more appropriate in some contexts (such as those where the chain of fluent
     * invocations is broken up due to necessity).
     * @see #then()
     * @throws SpecValidationException if the validation step incident to the state transition
     * ({@link SpecState#FLUID} to {@link SpecState#FROZEN}) fails.
     * @return the {@link OperationController} instance which owns this operation
     */
    OperationController<Z> done() throws SpecValidationException;
}
