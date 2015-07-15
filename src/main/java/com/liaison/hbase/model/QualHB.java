package com.liaison.hbase.model;

import com.liaison.hbase.model.ser.CellSerializable;

import java.util.EnumSet;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:04
 */
public interface QualHB extends NamedEntity, CellSerializable {
    EnumSet<VersioningModel> getVersioning();
}
