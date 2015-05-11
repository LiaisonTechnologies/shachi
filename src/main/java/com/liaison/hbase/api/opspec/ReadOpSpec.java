package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.Util;

public final class ReadOpSpec extends TableRowOpSpec<ReadOpSpec> implements Serializable {

    private static final long serialVersionUID = 1602390434837826147L;
    
    private LongValueSpec<ReadOpSpec> atTime;
    private final List<ColSpecRead<ReadOpSpec>> withColumn;
    
    @Override
    protected ReadOpSpec self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getTableRow(), this, "from", RowSpec.class);
        Util.validateAtLeastOne(getWithColumn(), this, "with", ColSpecRead.class);
    }

    public LongValueSpec<ReadOpSpec> getAtTime() {
        return this.atTime;
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
    public RowSpec<ReadOpSpec> from() throws IllegalArgumentException, IllegalStateException {
        final RowSpec<ReadOpSpec> rowSpec;
        rowSpec = new RowSpec<>(this);
        setTableRow(rowSpec);
        return rowSpec;
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
        final RowSpec<ReadOpSpec> tableRow;
        tableRow = getTableRow(); 
        if (tableRow != null) {
            Util.appendIndented(strGen,
                                getDepth() + 1,
                                "from table/row: ",
                                "\n",
                                tableRow,
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
    
    @Override
    protected boolean deepEquals(final OperationSpec<?> otherOpSpec) {
        final ReadOpSpec otherReadSpec;
        if (otherOpSpec instanceof ReadOpSpec) {
            otherReadSpec = (ReadOpSpec) otherOpSpec;
            /*
             * Equality checks ordered from least to most expensive, to allow for relatively quick
             * short-circuiting
             */
            return ((Util.refEquals(this.atTime, otherReadSpec.atTime))
                    &&
                    (Util.refEquals(getTableRow(), otherReadSpec.getTableRow()))
                    &&
                    (Util.refEquals(this.withColumn, otherReadSpec.withColumn)));
        }
        return false;
    }

    public ReadOpSpec(final Object handle, final HBaseContext context, final OperationController parent) {
        super(handle, context, parent);
        this.withColumn = new LinkedList<>();
    }
}
