package com.liaison.hbase.model.ser;

import com.liaison.hbase.exception.CellSerializationException;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 15:05
 */
@FunctionalInterface
public interface CellSerializer {
    /**
     *
     * @param source
     * @return
     * @throws CellSerializationException if an exception occurs in the process of converting the
     * source object to bytes
     * @throws ClassCastException if the type of the source object is not supported by this
     * serializer (in effect indicating that the associated field definition does not allow this
     * type of data)
     */
    byte[] serialize(Object source) throws CellSerializationException, ClassCastException;
}
