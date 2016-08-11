package com.liaison.shachi.model;

import com.liaison.shachi.model.ser.CellDeserializer;
import com.liaison.shachi.model.ser.CellSerializable;
import com.liaison.shachi.model.ser.CellSerializer;

import java.util.Map;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.19 17:02
 */
public interface FamilyHB extends NamedEntity, CellSerializable {
    Map<Name, QualModel> getQuals();
    boolean isClosedQualSet();
    VersioningModel getVersioning();
    CellSerializer getSerializer(QualHB forQualInstance);
    CellDeserializer getDeserializer(QualHB forQualInstance);
}
