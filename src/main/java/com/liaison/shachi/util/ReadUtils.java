/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
