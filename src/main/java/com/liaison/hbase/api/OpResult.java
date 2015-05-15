package com.liaison.hbase.api;

import java.io.Serializable;

import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.TableRowOpSpec;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.util.AbstractSelfRefBuilder;
import com.liaison.hbase.util.Util;

public class OpResult<O extends TableRowOpSpec<O>> implements Serializable {
    
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

    protected OpResult(final OpResultBuilder<O, ?, ?> build) {
        Util.ensureNotNull(build.originSpec, this, "originSpec", TableRowOpSpec.class);
        this.originSpec = build.originSpec;
        this.handle = this.originSpec.getHandle();
        this.tableRow = this.originSpec.getTableRow();
        this.hbExc = build.hbExc;
    }
}
