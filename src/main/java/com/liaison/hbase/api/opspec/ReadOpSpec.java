package com.liaison.hbase.api.opspec;

import java.util.LinkedList;
import java.util.List;

import com.liaison.hbase.util.Util;


public final class ReadOpSpec extends OperationSpec<ReadOpSpec> {

    private LongValueSpec<ReadOpSpec> atTime;
    private RowSpec<ReadOpSpec> fromTableRow;
    private List<ColSpecRead<ReadOpSpec>> withColumn;
    
    @Override
    protected ReadOpSpec self() { return this; }

    public LongValueSpec<ReadOpSpec> atTime() throws IllegalStateException {
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.atTime);
        this.atTime = new LongValueSpec<>(this);
        return this.atTime;
    }
    public RowSpec<ReadOpSpec> from() throws IllegalStateException {
        Util.validateExactlyOnce("fromTableRow", RowSpec.class, this.fromTableRow);
        this.fromTableRow = new RowSpec<>(this);
        return this.fromTableRow;
    }
    public ColSpecRead<ReadOpSpec> with() {
        final ColSpecRead<ReadOpSpec> withCol;
        withCol = new ColSpecRead<>(this);
        this.withColumn.add(withCol);
        return withCol;
    }

    public ReadOpSpec(final OperationController parent) {
        super(parent);
        this.withColumn = new LinkedList<>();
    }
}
