package com.liaison.shachi.dto;

import com.liaison.shachi.model.TableModel;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.06 13:24
 */
public interface RowRef {
    TableModel getTable();
    RowKey getRowKey();
    byte[] getLiteralizedRowKeyBytes() throws IllegalStateException;
}
