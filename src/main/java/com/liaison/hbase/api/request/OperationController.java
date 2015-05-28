/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.fluid.ReadOpSpecFluid;
import com.liaison.hbase.api.request.fluid.WriteOpSpecFluid;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.exception.HBaseUnsupportedOperationException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface OperationController {
    ReadOpSpecFluid read(Object handle) throws IllegalStateException, IllegalArgumentException;
    WriteOpSpecFluid write(Object handle) throws IllegalStateException, IllegalArgumentException;
    OpResultSet exec() throws HBaseUnsupportedOperationException, HBaseTableRowException, HBaseException;
}