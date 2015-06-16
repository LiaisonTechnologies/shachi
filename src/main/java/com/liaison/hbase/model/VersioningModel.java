package com.liaison.hbase.model;

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
}