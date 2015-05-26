/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.ReadOpSpec;
import com.liaison.hbase.dto.CellResult;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.util.Util;

public class ReadOpResult extends OpResult<ReadOpSpec> {

    private static final long serialVersionUID = 8027722441667395989L;

    public static class ReadOpResultBuilder extends OpResultBuilder<ReadOpSpec, ReadOpResult, ReadOpResultBuilder> {
        private static final String CLOSURENAME_ADD = ReadOpResultBuilder.class.getSimpleName() + "#add";
        
        private Map<FamilyQualifierPair, CellResult> data;
        
        @Override
        public final ReadOpResultBuilder self() {
            return this;
        }
        
        public ReadOpResultBuilder add(final FamilyQualifierPair fqp, final HBaseException exc) throws IllegalArgumentException {
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "exc", HBaseException.class);
            this.data.put(fqp, new CellResult(exc));
            return self();
        }
        public ReadOpResultBuilder add(final ColSpecRead<ReadOpSpec> colSpec, final HBaseException exc) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);
            return add(colSpec.toFQP(), exc);
        }
        
        public ReadOpResultBuilder add(final FamilyQualifierPair fqp, final Datum datum) throws IllegalArgumentException {
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "datum", Datum.class);
            this.data.put(fqp, new CellResult(datum));
            return this;
        }
        public ReadOpResultBuilder add(final ColSpecRead<ReadOpSpec> colSpec, final Datum datum) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);
            return add(colSpec.toFQP(), datum);
        }
        
        @Override
        public final ReadOpResult build() {
            return new ReadOpResult(this);
        }
        
        private ReadOpResultBuilder() {
            super();
            this.data = new HashMap<>();
        }
    }
    
    private static final String OPRESULT_TYPE_STR = "READ";
    
    public static ReadOpResultBuilder getBuilder() {
        return new ReadOpResultBuilder();
    }
    
    private final Map<FamilyQualifierPair, CellResult> data;

    public Map<FamilyQualifierPair, CellResult> getData() {
        return this.data;
    }
    public Datum getData(final FamilyQualifierPair fqp) throws HBaseException {
        final CellResult cellRes;
        final HBaseException hbExc;
        Datum result = null;
        
        cellRes = this.data.get(fqp);
        if (cellRes != null) {
            hbExc = cellRes.getExc();
            if (hbExc != null) {
                throw hbExc;
            }
            result = cellRes.getDatum();
        }
        return result;
    }
    
    @Override
    protected boolean deepEquals(OpResult<?> otherOpResult) {
        final ReadOpResult otherReadOpResult;
        if (otherOpResult instanceof ReadOpResult) {
            otherReadOpResult = (ReadOpResult) otherOpResult;
            return Util.refEquals(this.data, otherReadOpResult.data);
        }
        return false;
    }
    
    @Override
    protected String getOpResultTypeStr() {
        return OPRESULT_TYPE_STR;
    }
    
    @Override
    protected void prepareStrRepAdditional(StringBuilder strGen) {
        strGen.append("{data=");
        strGen.append(this.data);
        strGen.append("}");
    }
    
    private ReadOpResult(final ReadOpResultBuilder build) {
        super(build);
        
        String logMsg;
        
        if ((this.getException() != null) && (build.data.size() > 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " may not reference both a row-level exception and a non-empty data result set";
            throw new IllegalStateException(logMsg);
        }
        if ((this.getException() == null) && (build.data.size() <= 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " must reference exactly one of: a row-level exception or a data result set";
            throw new IllegalStateException(logMsg);
        }
        this.data = Collections.unmodifiableMap(build.data);
    }
}
