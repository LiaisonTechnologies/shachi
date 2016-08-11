/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

import com.liaison.shachi.model.TableModel;

public class HBaseNoSuchTableException extends HBaseException {

    private static final long serialVersionUID = -2202962723098585297L;
    
    private final TableModel model;
    
    public TableModel getModel() {
        return this.model;
    }
    
    public HBaseNoSuchTableException(final TableModel model, final String message) {
        super(message);
        this.model = model;
    }
    public HBaseNoSuchTableException(final TableModel model, final String message, final Throwable cause) {
        super(message, cause);
        this.model = model;
    }
    public HBaseNoSuchTableException(final TableModel model, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.model = model;
    }
}
