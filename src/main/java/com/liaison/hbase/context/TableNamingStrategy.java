package com.liaison.hbase.context;

import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;

public interface TableNamingStrategy {
    Name generate(TableModel model);
}
