package com.liaison.hbase.util;

public abstract class AbstractSelfRefBuilder<T, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRef<B> {

    public abstract T build();
}
