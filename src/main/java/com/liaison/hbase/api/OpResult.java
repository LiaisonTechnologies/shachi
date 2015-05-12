package com.liaison.hbase.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.TableRowOpSpec;
import com.liaison.hbase.dto.CellResult;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.util.Util;

public class OpResult<O extends TableRowOpSpec<O>> implements Serializable {
    
    private static final long serialVersionUID = -276369205056256487L;

    public static final class Builder<O extends TableRowOpSpec<O>> {
        private static final String CLOSURENAME_ADD = Builder.class.getSimpleName() + "#add";
        
        private O originSpec;
        private Map<FamilyQualifierPair, CellResult> data;
        private HBaseException hbExc;
        private boolean mutationPerformed;
        
        public Builder<O> origin(final O originSpec) {
            this.originSpec = originSpec;
            return this;
        }
        
        public Builder<O> exception(final HBaseException exc) {
            this.hbExc = exc;
            return this;
        }
        
        public Builder<O> mutationPerformed(final boolean mutationPerformed) {
            this.mutationPerformed = mutationPerformed;
            return this;
        }
        
        public Builder<O> add(final FamilyQualifierPair fqp, final HBaseException exc) throws IllegalArgumentException {
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "exc", HBaseException.class);
            this.data.put(fqp, new CellResult(exc));
            return this;
        }
        public Builder<O> add(final ColSpecRead<O> colSpec, final HBaseException exc) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);
            return add(colSpec.toFQP(), exc);
        }
        
        public Builder<O> add(final FamilyQualifierPair fqp, final Datum datum) throws IllegalArgumentException {
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "datum", Datum.class);
            this.data.put(fqp, new CellResult(datum));
            return this;
        }
        public Builder<O> add(final ColSpecRead<O> colSpec, final Datum datum) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);
            return add(colSpec.toFQP(), datum);
        }
        
        public OpResult<O> build() {
            return new OpResult<O>(this);
        }
        
        private Builder() {
            this.data = new LinkedHashMap<>();
            this.originSpec = null;
            this.hbExc = null;
            this.mutationPerformed = false;
        }
    }
    
    public static <O extends TableRowOpSpec<O>> Builder<O> getBuilder(final Class<O> origin) {
        return new Builder<O>();
    }
    
    private final O originSpec;
    private final Object handle;
    private final RowSpec<O> tableRow;
    private final Map<FamilyQualifierPair, CellResult> data;
    private final HBaseException hbExc;
    private final boolean mutationPerformed;

    public O getOrigin() {
        return this.originSpec;
    }
    public Object getHandle() {
        return this.handle;
    }
    public RowSpec<O> getTableRow() {
        return this.tableRow;
    }
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
    public HBaseException getException() {
        return this.hbExc;
    }
    public boolean isMutationPerformed() {
        return this.mutationPerformed;
    }

    private OpResult(final Builder<O> build) {
        String logMsg;
        
        Util.ensureNotNull(build.originSpec, this, "originSpec", TableRowOpSpec.class);
        if ((build.hbExc != null) && (build.data.size() > 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " may not reference both a row-level exception and a non-empty data result set";
            throw new IllegalStateException(logMsg);
        }
        if ((build.hbExc == null) && (build.data.size() <= 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " must reference exactly one of: a row-level exception or a data result set";
            throw new IllegalStateException(logMsg);
        }
        this.originSpec = build.originSpec;
        this.handle = this.originSpec.getHandle();
        this.tableRow = this.originSpec.getTableRow();
        this.hbExc = build.hbExc;
        this.mutationPerformed = build.mutationPerformed;
        this.data = Collections.unmodifiableMap(build.data);
    }
}
