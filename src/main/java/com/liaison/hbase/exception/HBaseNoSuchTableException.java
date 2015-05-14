package com.liaison.hbase.exception;

import com.liaison.hbase.model.TableModel;

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
