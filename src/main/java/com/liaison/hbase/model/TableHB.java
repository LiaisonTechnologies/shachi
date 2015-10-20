package com.liaison.hbase.model;

import com.liaison.hbase.model.ser.CellSerializable;

import java.util.Map;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:03
 */
public interface TableHB extends NamedEntity, CellSerializable {
    Map<Name, FamilyModel> getFamilies();
    FamilyModel getFamily(Name famName);
}
