package com.liaison.hbase.api;

import java.util.Collections;
import java.util.Map;

import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.TableRowOpSpec;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.HBaseException;

public class OpResult<O extends TableRowOpSpec<O>> {

    private static final class Builder<O extends TableRowOpSpec<O>> {
        private O originSpec;
        private Map<FamilyQualifierPair, Datum> data;
        private HBaseException hbExc;
        private boolean mutationPerformed;
    }
    
    private final O originSpec;
    private final Object handle;
    private final RowSpec<O> tableRow;
    private final Map<FamilyQualifierPair, Datum> data;
    private final HBaseException hbExc;
    private final boolean mutationPerformed;
    
    private OpResult(final Builder<O> build) {
        this.originSpec = build.originSpec;
        this.handle = this.originSpec.getHandle();
        this.tableRow = this.originSpec.getTableRow();
        this.hbExc = build.hbExc;
        this.mutationPerformed = build.mutationPerformed;
        this.data = Collections.unmodifiableMap(build.data);
    }
}
