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

import com.liaison.shachi.HBaseStart;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.exception.HBaseTableRowException;
import com.liaison.shachi.exception.HBaseUnsupportedOperationException;

import java.util.concurrent.Future;

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
