/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.Value;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.Util;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ColSpecWriteConfined implements ColSpecWriteFluid<ColSpecWriteConfined> {

    private final ColSpecWrite<WriteOpSpec> colSpecWrite;
    
    public ColSpecWriteConfined fam(final FamilyModel family) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.fam(family);
        return this;
    }
    public ColSpecWriteConfined qual(final QualModel qual) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.qual(qual);
        return this;
    }
    public ColSpecWriteConfined ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.ts(ts);
        return this;
    }
    public ColSpecWriteConfined value(final Value value) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.value(value);
        return this;
    }
    
    public ColSpecWriteConfined(final ColSpecWrite<WriteOpSpec> colSpecWrite) {
        Util.ensureNotNull(colSpecWrite, this, "colSpecWrite", ColSpecWrite.class);
        this.colSpecWrite = colSpecWrite;
    }

}
