/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface CondSpecFluid<C extends CondSpecFluid<C>> extends ColSpecFluid<C> {
    C row(RowKey rowKey) throws IllegalStateException, IllegalArgumentException;
    C value(Value value) throws IllegalStateException, IllegalArgumentException;
    C empty(Empty empty) throws IllegalStateException, IllegalArgumentException;
}
