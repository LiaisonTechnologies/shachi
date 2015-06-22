/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.response;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.impl.RowSpec;
import com.liaison.hbase.api.request.impl.TableRowOpSpec;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.util.AbstractSelfRefBuilder;

import java.io.Serializable;

public abstract class OpResult<O extends TableRowOpSpec<O>> implements Serializable {
    
    private static final long serialVersionUID = -276369205056256487L;

    protected abstract static class OpResultBuilder<O extends TableRowOpSpec<O>, T extends OpResult<O>, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {
        
        private O originSpec;
        private HBaseException hbExc;
        
        public B origin(final O originSpec) {
            this.originSpec = originSpec;
            return self();
        }
        
        public B exception(final HBaseException exc) {
            this.hbExc = exc;
            return self();
        }
        
        public abstract T build();
        
        protected OpResultBuilder() {
            this.originSpec = null;
            this.hbExc = null;
        }
    }
    
    private final O originSpec;
    private final Object handle;
    private final RowSpec<O> tableRow;
    private final HBaseException hbExc;
    
    private String strRep;
    private Integer hc;

    public O getOrigin() {
        return this.originSpec;
    }
    public Object getHandle() {
        return this.handle;
    }
    public RowSpec<O> getTableRow() {
        return this.tableRow;
    }
    public HBaseException getException() {
        return this.hbExc;
    }
    
    protected int deepHashCode() {
        return 0;
    }
    
    @Override
    public final int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = (Util.hashCode(this.handle) ^ deepHashCode());
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }
    
    protected abstract boolean deepEquals(OpResult<?> otherOpResult);
    
    @Override
    public final boolean equals(final Object otherObj) {
        final OpResult<?> otherOpResult;
        
        if (otherObj instanceof OpResult) {
            otherOpResult = (OpResult<?>) otherObj;
            /*
             * No need to compare the handle here, as OperationSpec compares it as part of its
             * equals method (at the beginning, once type is established).
             */
            return (Util.refEquals(this.originSpec, otherOpResult.originSpec)
                    &&
                    Util.refEquals(this.tableRow, otherOpResult.tableRow)
                    &&
                    Util.refEquals(this.hbExc, otherOpResult.hbExc)
                    &&
                    deepEquals(otherOpResult));
        }
        return false;
    }
    
    protected abstract String getOpResultTypeStr();
    protected abstract void prepareStrRepAdditional(StringBuilder strGen);
    
    @Override
    public final String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append("[");
            strGen.append(getOpResultTypeStr());
            strGen.append(":");
            strGen.append(this.handle);
            strGen.append("@");
            strGen.append(this.tableRow);
            strGen.append("]:");
            prepareStrRepAdditional(strGen);
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    protected OpResult(final OpResultBuilder<O, ?, ?> build) {
        Util.ensureNotNull(build.originSpec, this, "originSpec", TableRowOpSpec.class);
        this.originSpec = build.originSpec;
        this.handle = this.originSpec.getHandle();
        this.tableRow = this.originSpec.getTableRow();
        this.hbExc = build.hbExc;
    }
}
