/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import com.liaison.hbase.api.OpResultSet;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.exception.HBaseUnsupportedOperationException;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface OperationController {
    ReadOpSpec read(Object handle) throws IllegalStateException, IllegalArgumentException;
    WriteOpSpec write(Object handle) throws IllegalStateException, IllegalArgumentException;
    OpResultSet exec() throws HBaseUnsupportedOperationException, HBaseTableRowException, HBaseException;
}