package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public final class ReadOpSpec extends OperationSpec<ReadOpSpec, Get> implements Serializable {

    private static final long serialVersionUID = 1602390434837826147L;
    
    private LongValueSpec<ReadOpSpec> atTime;
    private RowSpec<ReadOpSpec> fromTableRow;
    private List<ColSpecRead<ReadOpSpec>> withColumn;
    
    @Override
    protected ReadOpSpec self() { return this; }

    @Override
    protected Get buildHBaseOp() {
        return null;
    }

    public LongValueSpec<ReadOpSpec> getAtTime() {
        return this.atTime;
    }
    public RowSpec<ReadOpSpec> getFromTableRow() {
        return this.fromTableRow;
    }
    public List<ColSpecRead<ReadOpSpec>> getWithColumn() {
        return this.withColumn;
    }
    
    public LongValueSpec<ReadOpSpec> atTime() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.atTime);
        this.atTime = new LongValueSpec<>(this);
        return this.atTime;
    }
    public RowSpec<ReadOpSpec> from() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("fromTableRow", RowSpec.class, this.fromTableRow);
        this.fromTableRow = new RowSpec<>(this);
        return this.fromTableRow;
    }
    public ColSpecRead<ReadOpSpec> with() {
        final ColSpecRead<ReadOpSpec> withCol;
        prepMutation();
        withCol = new ColSpecRead<>(this);
        this.withColumn.add(withCol);
        return withCol;
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        
    }

    public ReadOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
        this.withColumn = new LinkedList<>();
    }
}
