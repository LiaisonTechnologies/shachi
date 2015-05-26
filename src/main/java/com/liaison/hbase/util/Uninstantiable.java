/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

public abstract class Uninstantiable {
    protected Uninstantiable() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not instantiable: " + getClass().getName());
    }
}
