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
import com.liaison.hbase.api.request.impl.OperationControllerDefault;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface HBaseStart {
    /**
     * Begin the chain of fluent API invocations which will construct the specification of one or
     * more HBase operations to be executed. The {@link OperationControllerDefault} returned by this
     * method The chain ends (and the operations specified are
     * executed) upon invocation of 
     * @return
     */
    public OperationController begin();
}
