/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.api.request.frozen;

import com.liaison.shachi.api.request.impl.ReadOpSpecDefault;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.model.ColumnRange;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.VersioningModel;

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
    VersioningModel getCommonVersioningConfig() throws IllegalStateException;

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
