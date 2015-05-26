/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import java.util.List;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface WriteOpSpecFrozen extends TableRowOpSpecFrozen<WriteOpSpec> {
    CondSpec<WriteOpSpec> getGivenCondition();
    List<ColSpecWrite<WriteOpSpec>> getWithColumn();
}
