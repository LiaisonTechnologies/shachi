package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.model.VersioningModel;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.09 18:26
 */
public class ApplicableVersion {

    final LongValueSpecFrozen version;
    final Set<VersioningModel> scheme;

    public LongValueSpecFrozen getVersion() {
        return this.version;
    }
    public Set<VersioningModel> getScheme() {
        return this.scheme;
    }

    public ApplicableVersion(final EnumSet<VersioningModel> scheme, final LongValueSpecFrozen version) {
        Util.ensureNotNull(scheme, this, "scheme", EnumSet.class);
        Util.ensureNotNull(version, this, "version", LongValueSpecFrozen.class);
        this.scheme = Collections.unmodifiableSet(scheme);
        this.version = version;
        if (this.scheme.isEmpty()) {
            throw new IllegalArgumentException(ApplicableVersion.class.getSimpleName()
                                               + " must include a scheme with at least one "
                                               + VersioningModel.class.getSimpleName());
        }
    }
}
