/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.context;

import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.TableModel;

public interface TableNamingStrategy {
    Name generate(TableModel model);
}
