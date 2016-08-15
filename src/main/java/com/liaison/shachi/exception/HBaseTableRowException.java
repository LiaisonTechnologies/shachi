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

import com.liaison.shachi.dto.RowRef;

public class HBaseTableRowException extends HBaseException {

    private static final long serialVersionUID = -9220120682288168744L;
    
    private final RowRef tableRow;
    
    public RowRef getTableRow() {
        return this.tableRow;
    }
    
    public HBaseTableRowException(final RowRef tableRow, final String message) {
        super(message);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowRef tableRow, final String message, final Throwable cause) {
        super(message, cause);
        this.tableRow = tableRow;
    }
    public HBaseTableRowException(final RowRef tableRow, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.tableRow = tableRow;
    }
}
