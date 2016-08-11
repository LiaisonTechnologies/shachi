/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.frozen;

import com.liaison.shachi.dto.NullableValue;
import com.liaison.shachi.dto.RowKey;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface CondSpecFrozen {
    RowKey getRowKey();
    NullableValue getValue();
}
