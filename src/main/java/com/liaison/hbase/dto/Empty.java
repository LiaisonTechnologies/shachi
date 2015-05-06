package com.liaison.hbase.dto;

public class Empty extends NullableValue {

    private static final long serialVersionUID = 4938091499596277118L;

    public Empty(AbstractValueBuilder<?, ?> build) throws IllegalArgumentException {
        super(build);
        if (build.value != null) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                                               + " may only be constructed with a null "
                                               + byte[].class
                                               + " internal value");
        }
    }

}
