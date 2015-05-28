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

import com.liaison.hbase.api.request.impl.ReadOpSpec;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ReadOpSpecFrozen extends TableRowOpSpecFrozen<ReadOpSpec> {
    LongValueSpecFrozen getAtTime();
    List<? extends ColSpecReadFrozen> getWithColumn();
}
