/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.frozen;

import com.liaison.hbase.api.request.impl.ReadOpSpecDefault;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.model.ColumnRange;
import com.liaison.hbase.model.FamilyHB;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.VersioningModel;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: javadoc
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ReadOpSpecFrozen extends TableRowOpSpecFrozen<ReadOpSpecDefault> {

    /**
     * TODO: javadoc
     * @return
     * @throws IllegalStateException
     */
    EnumSet<VersioningModel> getCommonVersioningConfig() throws IllegalStateException;

    /**
     * TODO: javadoc
     * @return
     * @throws IllegalStateException
     */
    LongValueSpecFrozen getCommonVersion() throws IllegalStateException;

    /**
     * TODO: javadoc
     * @return
     */
    Integer getMaxEntriesPerFamily();

    /**
     * TODO: javadoc
     * @return
     */
    LongValueSpecFrozen getAtTime();

    /**
     * TODO: javadoc
     * @return
     */
    List<? extends ColSpecReadFrozen> getWithColumn();

    /**
     * TODO: javadoc
     * @param famModel
     * @param colSpecRead
     */
    void addColumnAssoc(FamilyHB famModel, ColSpecReadFrozen colSpecRead);

    /**
     * TODO: javadoc
     * @param fqp
     * @param colSpecRead
     */
    void addColumnAssoc(FamilyQualifierPair fqp, ColSpecReadFrozen colSpecRead);

    /**
     * TODO: javadoc
     * @param columnRange
     * @param colSpecRead
     */
    void addColumnRangeAssoc(ColumnRange columnRange, ColSpecReadFrozen colSpecRead);

    /**
     * TODO: javadoc
     * @param famModel
     * @return
     */
    Set<ColSpecReadFrozen> getColumnAssoc(FamilyHB famModel);

    /**
     * TODO: javadoc
     * @param fqp
     * @return
     */
    Set<ColSpecReadFrozen> getColumnAssoc(FamilyQualifierPair fqp);

    /**
     * TODO: javadoc
     * @param fqp
     * @return
     */
    Set<ColSpecReadFrozen> getColumnRangeAssoc(FamilyQualifierPair fqp);

    /**
     * TODO: javadoc
     * @return
     */
    Map<FamilyQualifierPair, Set<ColSpecReadFrozen>> getFamilyQualifierAssoc();

    /**
     * TODO: javadoc
     * @return
     */
    Map<FamilyHB, Set<ColSpecReadFrozen>> getFullFamilyAssoc();

    /**
     * TODO: javadoc
     * @return
     */
    Map<ColumnRange, Set<ColSpecReadFrozen>> getColumnRangeAssoc();
}
