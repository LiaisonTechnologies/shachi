package com.liaison.hbase.model.ser;

import com.liaison.hbase.exception.CellDeserializationException;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 15:09
 */
@FunctionalInterface
public interface CellDeserializer {
    /**
     *
     * @param hbaseCellValue
     * @return
     * @throws CellDeserializationException if the bytes retrieved from HBase cannot be decoded
     */
    Object deserialize(byte[] hbaseCellValue) throws CellDeserializationException;
}
