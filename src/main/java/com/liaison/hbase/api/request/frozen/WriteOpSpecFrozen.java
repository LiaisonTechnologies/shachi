/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.frozen;

import java.util.List;

import com.liaison.hbase.api.request.impl.WriteOpSpec;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface WriteOpSpecFrozen extends TableRowOpSpecFrozen<WriteOpSpec> {
    CondSpecFrozen getGivenCondition();
    List<? extends ColSpecWriteFrozen> getWithColumn();
}
