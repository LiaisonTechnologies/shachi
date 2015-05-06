package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.Util;

public final class ReadOpSpec extends OperationSpec<ReadOpSpec> implements Serializable {

    private static final long serialVersionUID = 1602390434837826147L;
    
    private LongValueSpec<ReadOpSpec> atTime;
    private RowSpec<ReadOpSpec> fromTableRow;
    private List<ColSpecRead<ReadOpSpec>> withColumn;
    
    @Override
    protected ReadOpSpec self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getFromTableRow(), this, "from", RowSpec.class);
        Util.validateAtLeastOne(getWithColumn(), this, "with", ColSpecRead.class);
    }

    public LongValueSpec<ReadOpSpec> getAtTime() {
        return this.atTime;
    }
    public RowSpec<ReadOpSpec> getFromTableRow() {
        return this.fromTableRow;
    }
    public List<ColSpecRead<ReadOpSpec>> getWithColumn() {
        return Collections.unmodifiableList(this.withColumn);
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
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:READ]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        if (this.fromTableRow != null) {
            Util.appendIndented(strGen,
                                getDepth() + 1,
                                "from table/row: ",
                                "\n",
                                this.fromTableRow,
                                "\n");
        }
        if (this.atTime != null) {
            Util.appendIndented(strGen,
                                getDepth() + 1,
                                "at (timestamp range): ",
                                "\n",
                                this.atTime,
                                "\n");
        }
        if (this.withColumn.size() > 0) {
            Util.appendIndented(strGen, getDepth() + 1, "with column(s): ", "\n");
            for (ColSpecRead<ReadOpSpec> colSpec : this.withColumn) {
                Util.appendIndented(strGen, getDepth() + 1, colSpec);
            }
        }
    }

    public ReadOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
        this.withColumn = new LinkedList<>();
    }
}
