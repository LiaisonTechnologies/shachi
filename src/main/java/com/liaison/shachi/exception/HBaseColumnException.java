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

public class HBaseColumnException extends HBaseTableRowException {
    
    private static final long serialVersionUID = 7523928044172073564L;
    
    private final ColRef colRef;
    
    public ColRef getColRef() {
        return this.colRef;
    }
    
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message) {
        super(rowRef, message);
        this.colRef = colRef;
    }
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause) {
        super(rowRef, message, cause);
        this.colRef = colRef;
    }
    public HBaseColumnException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, message, cause, enableSuppression, writableStackTrace);
        this.colRef = colRef;
    }
}
