package com.liaison.hbase.dto;

import com.liaison.hbase.model.FamilyHB;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualHB;
import com.liaison.hbase.model.QualModel;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.06 13:25
 */
public interface ColRef {
    FamilyHB getFamily();
    QualHB getColumn();
}
