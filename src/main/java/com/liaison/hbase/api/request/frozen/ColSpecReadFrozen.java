/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.frozen;

import com.liaison.hbase.api.request.impl.LongValueSpec;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ColSpecReadFrozen extends ColSpecFrozen {

    /**
     * TODO
     * @return
     */
    LongValueSpec<?> getVersion();

    /**
     * TODO
     * @return
     */
    boolean isOptional();
}
