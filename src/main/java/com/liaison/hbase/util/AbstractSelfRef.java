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
