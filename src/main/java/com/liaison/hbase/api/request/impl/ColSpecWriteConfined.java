/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.fluid.ColSpecWriteFluid;
import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;

import java.util.concurrent.TimeUnit;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ColSpecWriteConfined implements ColSpecWriteFluid<ColSpecWriteConfined> {

    private final ColSpecWrite<WriteOpSpecDefault> colSpecWrite;

    @Override
    public ColSpecWriteConfined handle(Object handle) throws IllegalStateException {
        colSpecWrite.handle(handle);
        return this;
    }
    @Override
    public ColSpecWriteConfined fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.fam(family);
        return this;
    }
    @Override
    public ColSpecWriteConfined qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.qual(qual);
        return this;
    }
    @Override
    public ColSpecWriteConfined version(final long version) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.version(version);
        return this;
    }
    @Override
    public ColSpecWriteConfined ttl(final long ttlValue, final TimeUnit ttlUnit) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.ttl(ttlValue, ttlUnit);
        return this;
    }
    @Override
    public ColSpecWriteConfined ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.ts(ts);
        return this;
    }
    @Override
    public ColSpecWriteConfined value(final Value value) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.value(value);
        return this;
    }
    @Override
    public ColSpecWriteConfined empty(final Empty empty) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.empty(empty);
        return this;
    }
    @Override
    public ColSpecWriteConfined content(final Object dataObj) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.content(dataObj);
        return this;
    }

    public ColSpecWriteConfined(final ColSpecWrite<WriteOpSpecDefault> colSpecWrite) {
        Util.ensureNotNull(colSpecWrite, this, "colSpecWrite", ColSpecWrite.class);
        this.colSpecWrite = colSpecWrite;
    }

}
