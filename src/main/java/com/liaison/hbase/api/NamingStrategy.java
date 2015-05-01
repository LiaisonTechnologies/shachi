package com.liaison.hbase.api;

import com.liaison.hbase.model.Name;

public interface NamingStrategy<K, F> {
    F buildName(Name base, K key);
}
