package com.liaison.hbase.context;

import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;

public class IdentityTableNamingStrategy implements TableNamingStrategy {
    @Override
    public Name generate(final TableModel model) {
        // ta-daaaaa!
        return model.getName();
    }
}
