package com.liaison.hbase.model;

import java.util.EnumSet;
import java.util.Map;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:02
 */
public interface FamilyHB extends NamedEntity {
    Map<Name, QualModel> getQuals();
    boolean isClosedQualSet();
    EnumSet<VersioningModel> getVersioning();
}
