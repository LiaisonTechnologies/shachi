package com.liaison.hbase.api.opspec;

import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.hbase.client.Put;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.util.Util;

public class WriteOpSpec extends OperationSpec<WriteOpSpec, Put> implements Serializable {

    private static final long serialVersionUID = 2572256818666730468L;
    private CondSpec<WriteOpSpec> givenCondition;
    private RowSpec<WriteOpSpec> onTableRow;
    private List<ColSpecWrite<WriteOpSpec>> withColumn;
    
    @Override
    public WriteOpSpec self() { return this; }

    @Override
    protected Put buildHBaseOp() {
        return null;
    }
    
    public CondSpec<WriteOpSpec> getGivenCondition() {
        return this.givenCondition;
    }
    public RowSpec<WriteOpSpec> getOnTableRow() {
        return this.onTableRow;
    }
    public List<ColSpecWrite<WriteOpSpec>> getWithColumn() {
        return this.withColumn;
    }

    public RowSpec<WriteOpSpec> on() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("onTableRow", RowSpec.class, this.onTableRow);
        this.onTableRow = new RowSpec<>(this);
        return this.onTableRow;
    }
    public CondSpec<WriteOpSpec> given() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("givenCondition", CondSpec.class, this.givenCondition);
        this.givenCondition = new CondSpec<>(this);
        return this.givenCondition;
    }
    public ColSpecWrite<WriteOpSpec> with() {
        final ColSpecWrite<WriteOpSpec> withCol;
        prepMutation();
        withCol = new ColSpecWrite<>(this);
        this.withColumn.add(withCol);
        return withCol;
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:WRITE]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen) {
        if (this.onTableRow != null) {
            Util.appendIndented(strGen,
                                getDepth() + 1,
                                "on table/row: ",
                                "\n",
                                this.onTableRow,
                                "\n");
        }
        if (this.givenCondition != null) {
            Util.appendIndented(strGen,
                                getDepth() + 1,
                                "given condition: ",
                                "\n",
                                this.givenCondition,
                                "\n");
        }
        if (this.withColumn.size() > 0) {
            Util.appendIndented(strGen, getDepth() + 1, "with column(s): ", "\n");
            for (ColSpecWrite<WriteOpSpec> colSpec : this.withColumn) {
                Util.appendIndented(strGen, getDepth() + 1, colSpec);
            }
        }
    }

    public WriteOpSpec(final HBaseContext context, final OperationController parent) {
        super(context, parent);
    }
}
