/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.util;

import com.liaison.javabasics.commons.Uninstantiable;
import com.liaison.shachi.api.request.frozen.LongValueSpecFrozen;
import org.apache.hadoop.hbase.client.Get;

import java.io.IOException;

public final class ReadUtils extends Uninstantiable {

    /*
     * TODO: extract more of what is currently in HBaseControl.HBaseDelegate into this class
     */

    public static void applyTS(final Get get, final LongValueSpecFrozen atTime) throws IOException {
        final Long lowerIncObj;
        final Long upperExcObj;
        final long min;
        final long max;

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
