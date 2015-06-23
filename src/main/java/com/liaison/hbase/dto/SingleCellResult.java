/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;

import com.liaison.hbase.exception.HBaseException;

import java.io.Serializable;

public class SingleCellResult extends CellResult<Datum> implements Serializable {

    private static final long serialVersionUID = 5186988866264811933L;

    public SingleCellResult(final HBaseException exc) throws IllegalArgumentException {
        super(exc);
    }
    public SingleCellResult(final Datum datum) throws IllegalArgumentException {
        super(datum);
    }
    public SingleCellResult() {
        super();
    }
}
