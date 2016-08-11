package com.liaison.shachi.model.ser;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 17:37
 */
public interface CellSerializable {
    CellSerializer getSerializer();
    CellDeserializer getDeserializer();
}
