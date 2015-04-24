package com.liaison.hbase.util;

public abstract class Uninstantiable {
    protected Uninstantiable() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not instantiable: " + getClass().getName());
    }
}
