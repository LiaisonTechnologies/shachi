/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.frozen;

import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ColSpecFrozen {
    Object getHandle();
    FamilyModel getFamily();
    QualModel getColumn();
}
