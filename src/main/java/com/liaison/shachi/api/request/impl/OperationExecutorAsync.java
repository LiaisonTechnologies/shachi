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
package com.liaison.shachi.api.request.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.request.OperationController;
import com.liaison.shachi.api.request.OperationExecutor;
import com.liaison.shachi.api.response.OpResultSet;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class OperationExecutorAsync implements OperationExecutor<ListenableFuture<OpResultSet>> {

    private final OperationController<OpResultSet> coreOperation;
    private final HBaseControl.HBaseDelegate delegate;

    /**
     * {@inheritDoc}
     * @see com.liaison.shachi.api.request.OperationController#exec()
     */
    @Override
    public ListenableFuture<OpResultSet> exec() {
        return this.delegate.execAsync(this.coreOperation::exec);
    }

    /**
     * 
     */
    public OperationExecutorAsync(final HBaseControl.HBaseDelegate delegate, final OperationController<OpResultSet> coreOperation) {
        Util.ensureNotNull(delegate, this, "delegate", HBaseControl.HBaseDelegate.class);
        this.delegate = delegate;
        Util.ensureNotNull(coreOperation, this, "coreOperation", OperationController.class);
        this.coreOperation = coreOperation;
    }

}
