package com.liaison.hbase.api.opspec;

public interface SingleOpSpecCreator {
    ReadOpSpec read();
    CreateOpSpec create();
    UpdateOpSpec update();
    DeleteOpSpec delete();
}
