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
import com.liaison.hbase.api.request.impl.SpecState;
import com.liaison.hbase.api.request.impl.StatefulSpec;
import com.liaison.hbase.exception.SpecValidationException;

import java.util.Collection;

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

    private SpecUtil() {}
}
