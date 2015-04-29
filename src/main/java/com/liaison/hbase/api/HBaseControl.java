package com.liaison.hbase.api;

import com.liaison.hbase.api.opspec.OperationController;
import com.liaison.hbase.context.DefaultHBaseContext;

public class HBaseControl {
    
    public OperationController now() {
        return new OperationController(DefaultHBaseContext.getBuilder().build());
    }
}
