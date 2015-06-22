/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

public abstract class AbstractSelfRefBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRef<B> {

    public abstract T build();
}
