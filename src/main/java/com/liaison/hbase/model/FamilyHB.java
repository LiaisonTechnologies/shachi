package com.liaison.hbase.model;

import com.liaison.hbase.model.ser.CellDeserializer;
import com.liaison.hbase.model.ser.CellSerializable;
import com.liaison.hbase.model.ser.CellSerializer;

import java.util.EnumSet;
import java.util.Map;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:02
 */
public interface FamilyHB extends NamedEntity, CellSerializable {
    Map<Name, QualModel> getQuals();
    boolean isClosedQualSet();
    EnumSet<VersioningModel> getVersioning();
    CellSerializer getSerializer(QualHB forQualInstance);
    CellDeserializer getDeserializer(QualHB forQualInstance);
}
