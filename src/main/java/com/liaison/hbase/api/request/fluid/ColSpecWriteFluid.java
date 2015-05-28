/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.fluid;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface ColSpecWriteFluid<C extends ColSpecWriteFluid<C>> {
    C fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException;
    C qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException;
    C ts(final long ts) throws IllegalStateException, IllegalArgumentException;
    C value(final Value value) throws IllegalStateException, IllegalArgumentException;
}
