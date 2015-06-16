package com.liaison.hbase.model;

import com.google.common.collect.Sets;
import jdk.nashorn.internal.runtime.Version;

import java.util.EnumSet;
import java.util.Set;

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

    private static boolean setsOverlap(final Set<VersioningModel> set1, final Set<VersioningModel> set2) {
        return !Sets.intersection(set1, set2).isEmpty();
    }

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
     * @param verModelSet
     * @return
     */
    public static boolean isTimestampBased(final EnumSet<VersioningModel> verModelSet) {
        return setsOverlap(SET_TIMESTAMP, verModelSet);
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
     * @param verModelSet
     * @return
     */
    public static boolean isQualifierBased(final EnumSet<VersioningModel> verModelSet) {
        return setsOverlap(SET_QUALIFIER, verModelSet);
    }
}
