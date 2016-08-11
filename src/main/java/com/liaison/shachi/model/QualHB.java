package com.liaison.shachi.model;

import com.liaison.shachi.model.ser.CellSerializable;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:04
 */
public interface QualHB extends NamedEntity, CellSerializable {
    VersioningModel getVersioning();
}
