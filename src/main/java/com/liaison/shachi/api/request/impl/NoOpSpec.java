/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.impl;

import com.liaison.shachi.context.HBaseContext;

import java.io.Serializable;

public class NoOpSpec extends OperationSpec<NoOpSpec> implements Serializable {

    private static final long serialVersionUID = 1682509457085206829L;

    @Override
    public NoOpSpec self() { return this; }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:NONE]";
    }
    
    @Override
    protected boolean deepEquals(final OperationSpec<?> otherOpSpec) {
        return (otherOpSpec instanceof NoOpSpec);
    }
    
    public NoOpSpec(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(handle, context, parent);
    }
}
