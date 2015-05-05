package com.liaison.hbase.util;

import java.util.EnumSet;

public enum DefensiveCopyStrategy {
    NEVER, GET, SET, ALWAYS;
    
    public static final DefensiveCopyStrategy DEFAULT = ALWAYS;
    
    public static final EnumSet<DefensiveCopyStrategy> COPY_ON_GET = EnumSet.of(GET, ALWAYS);
    public static final EnumSet<DefensiveCopyStrategy> COPY_ON_SET = EnumSet.of(SET, ALWAYS);
}
