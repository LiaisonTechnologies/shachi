/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase;

import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.request.OperationExecutor;

/**
 * Denotes the starting point of a chain of HBase API operations.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <X> The type of the result which will be returned once the chain of operations is
 * executed.
 */
public interface HBaseStart<X> {
    /**
     * Begin the chain of fluent API invocations which will construct the specification of one or
     * more HBase operations to be executed. The {@link OperationController} returned by this
     * method will return an instance of the parameterized type upon invocation of
     * {@link OperationExecutor#exec()} at the end of the specification chain. 
     * @return an {@link OperationController} which will permit an HBase operation spec chain to be
     * defined which will return an instance of the parameterized type upon execution.
     */
    public OperationController<X> begin();
}
