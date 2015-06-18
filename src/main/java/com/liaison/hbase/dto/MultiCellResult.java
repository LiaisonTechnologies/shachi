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
import java.util.List;

public class MultiCellResult extends CellResult<List<Datum>> implements Serializable {

    public MultiCellResult(final HBaseException exc) throws IllegalArgumentException {
        super(exc);
    }
    public MultiCellResult(final List<Datum> dataList) throws IllegalArgumentException {
        super(dataList);
    }
    public MultiCellResult() {
        super();
    }
}
