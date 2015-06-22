/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

/**
 * Abstract representation of a type which implements a method {@link #self()} which always returns
 * a reference to the instance, with the most specific type information bound to it.
 * <br><br>
 * Useful when implementing chained APIs with inheritance; chained operations typically must return
 * a reference to self upon completion, such that subsequent operations may be performed inline.
 * However, operations specified by supertypes do not have the subtype type information needed in
 * order to return a properly-typed reference to 'this'. Using AbstractSelfRef as the root of the
 * inheritance tree forces leaf/terminal implementations to implement the {@link #self()} method,
 * whose implementation should *always* return 'this', at the most specific level. Consequently,
 * superclass methods which need to return a self-reference can then return the result of a
 * {@link #self()} call in order to return a typed reference to the instance.
 * 
 * @see {@link http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ206}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <A> Self-referential generic type representing the specific implementation to be returned
 * by {@link #self()}.
 */
public abstract class AbstractSelfRef<A extends AbstractSelfRef<A>> {
    
    /**
     * Return a reference to 'this', affixed with appropriate type bindings for the current
     * instance. <strong>Implementations must always return <code>this</code>; the following is the
     * <em>only</em> valid implementation of this method:</strong>
     * <br><br>
     * <code>
     * return this;
     * </code>
     * <br><br>
     * {@link #AbstractSelfRef() The AbstractSelfRef constructor} verifies (using == comparison)
     * that this method returns a reference which points to the same object as <code>this</code>;
     * any implementation which <em>does not</em> return (typed) <code>this</code> will throw
     * {@link IllegalStateException} upon instantiation.
     * @return A reference to <code>this</code> instance, affixed with the implementation's type
     * information.
     */
    protected abstract A self();
    
    /**
     * Ensure that the implementation of {@link #self()} return a reference to <code>this</code>.
     * @see {@link #self()}
     * @throws IllegalStateException if <code>{@link #self()} != this</code>.
     */
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
