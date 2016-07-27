package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.model.VersioningModel;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.09 18:26
 */
public class ApplicableVersion {

    final LongValueSpecFrozen version;
    final VersioningModel scheme;

    public LongValueSpecFrozen getVersion() {
        return this.version;
    }
    public VersioningModel getScheme() {
        return this.scheme;
    }

    public ApplicableVersion(final VersioningModel scheme, final LongValueSpecFrozen version) {
        Util.ensureNotNull(scheme, this, "scheme", VersioningModel.class);
        Util.ensureNotNull(version, this, "version", LongValueSpecFrozen.class);
        this.scheme = scheme;
        this.version = version;
    }
}
