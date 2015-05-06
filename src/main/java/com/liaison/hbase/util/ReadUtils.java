package com.liaison.hbase.util;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Get;

import com.liaison.hbase.api.opspec.LongValueSpec;
import com.liaison.hbase.api.opspec.ReadOpSpec;

public final class ReadUtils {

    public static void applyTS(final Get get, final ReadOpSpec readSpec) throws IOException {
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
