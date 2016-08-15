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
package com.liaison.shachi.exception;

import com.liaison.shachi.dto.ColRef;
import com.liaison.shachi.dto.RowRef;

public class HBaseEmptyResultSetException extends HBaseMultiColumnException {
    
    private static final long serialVersionUID = 1935706509279956966L;
    
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message) {
        super(rowRef, colRefList, message);
    }
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message, final Throwable cause) {
        super(rowRef, colRefList, message, cause);
    }
    public HBaseEmptyResultSetException(final RowRef rowRef, final Iterable<? extends ColRef> colRefList, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(rowRef, colRefList, message, cause, enableSuppression, writableStackTrace);
    }
}
