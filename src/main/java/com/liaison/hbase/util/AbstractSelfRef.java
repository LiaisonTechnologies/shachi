/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

public abstract class AbstractSelfRef<A extends AbstractSelfRef<A>> {
    protected abstract A self();
    protected AbstractSelfRef() throws IllegalStateException {
        // Explanation of the (relatively-rare) object comparison using == (or !=): need to ensure
        // that the inheriting implementation still returns the same object (i.e. generic-typed
        // "this"). So, want to make sure that self() returns EXACTLY THE SAME object, not just an
        // equivalent object.
        if (self() != this) {
            throw new IllegalStateException("self() method implementation must return identity");
        }
    }
}
