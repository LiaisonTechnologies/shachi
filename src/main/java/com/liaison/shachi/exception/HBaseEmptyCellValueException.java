/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

import com.liaison.shachi.dto.ColRef;
import com.liaison.shachi.dto.RowRef;

public class HBaseEmptyCellValueException extends HBaseColumnException {
    
    private static final long serialVersionUID = 8167381539562556543L;
    
    public HBaseEmptyCellValueException(final RowRef rowRef, final ColRef colRef, String message) {
        super(rowRef, colRef, message);
    }
    public HBaseEmptyCellValueException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause) {
        super(rowRef, colRef, message, cause);
    }
    public HBaseEmptyCellValueException(final RowRef rowRef, final ColRef colRef, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(rowRef, colRef, message, cause, enableSuppression, writableStackTrace);
    }
}
