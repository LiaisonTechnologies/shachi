package com.liaison.hbase.model;

import java.util.EnumSet;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:04
 */
public interface QualHB extends NamedEntity {
    EnumSet<VersioningModel> getVersioning();
}
