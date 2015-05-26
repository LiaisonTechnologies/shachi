/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import java.util.EnumSet;

public enum DefensiveCopyStrategy {
    NEVER, GET, SET, ALWAYS;
    
    public static final DefensiveCopyStrategy DEFAULT = ALWAYS;
    
    public static final EnumSet<DefensiveCopyStrategy> COPY_ON_GET = EnumSet.of(GET, ALWAYS);
    public static final EnumSet<DefensiveCopyStrategy> COPY_ON_SET = EnumSet.of(SET, ALWAYS);
}
