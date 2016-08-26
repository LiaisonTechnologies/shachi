/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.api.response;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.api.request.impl.TableRowOpSpec;
import com.liaison.shachi.dto.TableRow;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.util.AbstractSelfRefBuilder;

import java.io.Serializable;

public abstract class OpResult<O extends TableRowOpSpec<O>> implements Serializable {
    
    private static final long serialVersionUID = -276369205056256487L;

    protected abstract static class OpResultBuilder<O extends TableRowOpSpec<O>, T extends OpResult<O>, B extends AbstractSelfRefBuilder<T, B>> extends AbstractSelfRefBuilder<T, B> {

        private TableRow tableRow;
        private O originSpec;
        private HBaseException hbExc;

        public B origin(final O originSpec) {
            this.originSpec = originSpec;
            this.tableRow =
                TableRow.of(originSpec.getTableRow().getTable(),
                            originSpec.getTableRow().getRowKey());
            return self();
        }
        
        public B exception(final HBaseException exc) {
            this.hbExc = exc;
            return self();
        }

        protected TableRow getTableRow() throws IllegalStateException {
            final String logMsg;
            if (this.tableRow == null) {
                logMsg = "Illegal attempt to access table row before invoking origin()";
                throw new IllegalStateException(logMsg);

            }
            return this.tableRow;
        }

        public abstract T build();
        
        protected OpResultBuilder() {
            this.originSpec = null;
            this.tableRow = null;
            this.hbExc = null;
        }
    }


    private final O originSpec;
    private final Object handle;
    private final TableRow tableRow;
    private final HBaseException hbExc;
    
    private String strRep;
    private Integer hc;

    public O getOrigin() {
        return this.originSpec;
    }
    public Object getHandle() {
        return this.handle;
    }
    public TableRow getTableRow() {
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
        Util.ensureNotNull(build.tableRow, this, "tableRow", TableRow.class);
        this.originSpec = build.originSpec;
        this.tableRow = build.tableRow;
        this.handle = this.originSpec.getHandle();
        this.hbExc = build.hbExc;
    }
}
