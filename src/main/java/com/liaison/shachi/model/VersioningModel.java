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

package com.liaison.shachi.model;

import com.liaison.shachi.api.request.impl.CriteriaSpec;
import com.liaison.shachi.api.request.impl.LongValueSpec;

import java.util.EnumSet;

/**
 * Specifies how the version number will be affixed to cells in this column family.
 */
public enum VersioningModel {
    /**
     * Affixes the version number as the timestamp of the cell, but subtracts the version number
     * from Long.MAX_VALUE first so that when the value for the column is retrieved, the oldest
     * value is retrieved first.
     * <br /><br />
     * Note that if the column family is not set up to retain an infinite number of versions, then
     * this strategy could lead to recent cells beyond the version retention threshold being
     * silently dropped during the compaction process. In particular, it may become impossible to
     * consistently/durably add new values to the column family, as the newer values will become
     * eligible for cleanup as soon as they are written, by virtue of having a lower timestamp
     * value (as derived from the higher version number).
     */
    TIMESTAMP_CHRONO,
    /**
     * Affixes the version number as the timestamp of the cell as-is, such that higher version
     * numbers are retrieved first.
     * <br /><br />
     * Note that if the column family is not set up to retain an infinite number of versions, then
     * this strategy could lead to older cells beyond the version retention threshold being silently
     * dropped during the compaction process.
     */
    TIMESTAMP_LATEST,
    /**
     * Affixes the version number (as-is) as a suffix of the qualifier value.
     */
    QUALIFIER_CHRONO,
    /**
     * Affixes the version number, reversed as Long.MAX_VALUE - version, as a suffix of the
     * qualifier value.
     */
    QUALIFIER_LATEST;

    /**
     * TODO
     */
    public static EnumSet<VersioningModel> SET_TIMESTAMP =
        EnumSet.of(TIMESTAMP_CHRONO,
                   TIMESTAMP_LATEST);

    /**
     * TODO
     */
    public static EnumSet<VersioningModel> SET_QUALIFIER =
        EnumSet.of(QUALIFIER_CHRONO,
                   QUALIFIER_LATEST);

    /**
     * TODO
     */
    public static EnumSet<VersioningModel> SET_INVERTING =
        EnumSet.of(QUALIFIER_LATEST, TIMESTAMP_CHRONO);

    /**
     * TODO
     * @param verModel
     * @return
     */
    public static boolean isTimestampBased(final VersioningModel verModel) {
        return SET_TIMESTAMP.contains(verModel);
    }

    /**
     * TODO
     * @param verModel
     * @return
     */
    public static boolean isQualifierBased(final VersioningModel verModel) {
        return SET_QUALIFIER.contains(verModel);
    }

    /**
     * TODO
     * @param verModel
     * @return
     */
    public static boolean isInverting(final VersioningModel verModel) {
        return SET_INVERTING.contains(verModel);
    }

    public static <P extends CriteriaSpec<P, ?>> LongValueSpec<P> buildLongValueSpecForQualVersioning(final P parent) {
        return new LongValueSpec<>(parent, Long.valueOf(0), null);
    }
}
