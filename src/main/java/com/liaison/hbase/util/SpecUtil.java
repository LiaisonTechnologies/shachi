/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import com.liaison.commons.Uninstantiable;
import com.liaison.commons.Util;
import com.liaison.hbase.api.request.frozen.ColSpecFrozen;
import com.liaison.hbase.api.request.impl.SpecState;
import com.liaison.hbase.api.request.impl.StatefulSpec;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyHB;
import com.liaison.hbase.model.QualHB;
import com.liaison.hbase.model.VersioningModel;

import java.util.Collection;
import java.util.EnumSet;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public final class SpecUtil extends Uninstantiable {

    public static <X> void validateRequired(final X specValue, final StatefulSpec<?, ?> spec, final String fieldName, final Class<? super X> fieldType) throws SpecValidationException {
        try {
            Util.ensureNotNull(specValue, spec, fieldName, fieldType);
        } catch (IllegalArgumentException iaExc) {
            throw new SpecValidationException(spec.getState(), SpecState.FROZEN, spec, iaExc.getMessage());
        }
    }
    public static <X> void validateAtLeastOne(final Collection<X> specValue, final StatefulSpec<?, ?> spec, final String fieldName, final Class<?> fieldType) throws SpecValidationException {
        try {
            Util.ensureNotNull(specValue, spec, fieldName);
        } catch (IllegalArgumentException iaExc) {
            throw new SpecValidationException(spec.getState(), SpecState.FROZEN, spec, iaExc.getMessage());
        }
        if (specValue.size() <= 0) {
            throw new SpecValidationException(spec.getState(),
                    SpecState.FROZEN,
                    spec,
                    ("At least one "
                            + fieldName
                            + " ("
                            + fieldType.getSimpleName()
                            + ") is required"));
        }
    }

    /**
     * Determine the versioning configuration for the current read column spec. If a versioning
     * configuration is declared at the qualifier level, use it; otherwise, default to the family
     * level, if a configuration is defined there. Note that in some cases, neither the family
     * model nor the qualifier model will define a versioning configuration, so this method may
     * return null.
     * @param colFam
     * @param colQual
     * @return
     */
    public static EnumSet<VersioningModel> determineVersioningScheme(final FamilyHB colFam, final QualHB colQual) {
        EnumSet<VersioningModel> versioningScheme = null;

        if (colQual != null) {
            versioningScheme = colQual.getVersioning();
        }
        if (versioningScheme == null) {
            if (colFam != null) {
                versioningScheme = colFam.getVersioning();
            }
        }
        return versioningScheme;
    }

    public static EnumSet<VersioningModel> determineVersioningScheme(final ColSpecFrozen colSpec) {
        return determineVersioningScheme(colSpec.getFamily(), colSpec.getColumn());
    }

    private SpecUtil() {}
}
