/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import java.util.concurrent.Future;

import com.liaison.hbase.HBaseStart;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.exception.HBaseUnsupportedOperationException;

/**
 * The terminus of the chain of HBase operation-specification invocations, at which point the spec
 * chain is executed. The return value is an instance of the parameterized type (which may be an
 * asynchronous {@link Future}, in some implementations).
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <X> The operation result type to be returned when the chain of operations is executed.
 */
public interface OperationExecutor<X> {
    /**
     * Execute a sequence of HBase operations. Depending on the implementation at the time of exec
     * (as determined by the starting {@link HBaseStart} implementation and the preceding sequence
     * of operations), this method may return an asynchronous {@link Future}, whereby the actual
     * result may be obtained via {@link Future#get()}.
     * @return The result of executing the chain of HBase operations specified.
     * @throws HBaseUnsupportedOperationException if the controller executing the sequence of
     * specified HBase operations encounters one which the controller does not support.
     * @throws HBaseTableRowException if an HBase exception is encountered during a read or write
     * incident to a particular row of a table
     * @throws HBaseException if any other HBase exception is encountered in the course of
     * executing the operation specification chain
     */
    X exec() throws HBaseUnsupportedOperationException, HBaseTableRowException, HBaseException;
}
