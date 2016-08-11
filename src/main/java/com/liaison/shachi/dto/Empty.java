/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.dto;


public final class Empty extends NullableValue {

    private static final long serialVersionUID = 4938091499596277118L;

    private static final class Builder extends AbstractValueBuilder<Empty, Builder> {
        @Override
        protected Builder self() {
            return this;
        }
        @Override
        public Empty build() {
            return new Empty(this);
        }
    }

    private static final Empty INSTANCE;
    private static final String STR_REP = "--empty--";

    public static Empty getInstance() {
        return INSTANCE;
    }
    static {
        INSTANCE = new Empty.Builder().build();
    }

    private String strRep;
    
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public boolean equals(final Object otherObj) {
        return (otherObj instanceof Empty);
    }
    @Override
    public String toString() {
        if (this.strRep == null) {
            this.strRep = buildStrRep((strGen) -> { strGen.append(STR_REP); });
        }
        return this.strRep;
    }
    
    private Empty(AbstractValueBuilder<?, ?> build) throws IllegalArgumentException {
        super(build);
        if (build.value != null) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                                               + " may only be constructed with a null "
                                               + byte[].class
                                               + " internal value");
        }
    }
}
