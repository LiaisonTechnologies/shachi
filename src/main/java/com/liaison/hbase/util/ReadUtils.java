/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import com.liaison.hbase.api.request.impl.LongValueSpec;
import com.liaison.hbase.api.request.impl.ReadOpSpecDefault;
import org.apache.hadoop.hbase.client.Get;

import java.io.IOException;

public final class ReadUtils {

    public static void applyTS(final Get get, final ReadOpSpecDefault readSpec) throws IOException {
        final LongValueSpec<?> atTime;
        final Long lowerIncObj;
        final Long upperExcObj;
        final long min;
        final long max;
        
        atTime = readSpec.getAtTime();
        if (atTime != null) {
            lowerIncObj = atTime.getLowerBoundInclusive();
            upperExcObj = atTime.getUpperBoundExclusive();
            min = atTime.getTypeMin();
            max = atTime.getTypeMax();
        
            if ((lowerIncObj != null) || (upperExcObj != null)) {
                if (lowerIncObj == null) {
                    get.setTimeRange(min, upperExcObj.longValue());
                } else if (upperExcObj == null) {
                    get.setTimeRange(lowerIncObj.longValue(), max);
                } else if ((upperExcObj.longValue() - lowerIncObj.longValue()) == 1) {
                    get.setTimeStamp(lowerIncObj.longValue());
                } else {
                    get.setTimeRange(lowerIncObj.longValue(), upperExcObj.longValue());
                }
            }
        }
    }
    
    private ReadUtils() { }
}
