/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import com.liaison.hbase.api.request.ReadOpSpec;
import com.liaison.hbase.api.request.fluid.ColSpecReadFluid;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.StringRepFormat;
import com.liaison.hbase.util.Util;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public final class ReadOpSpecDefault extends TableRowOpSpec<ReadOpSpecDefault> implements ReadOpSpec<OpResultSet>, Serializable {

    private static final long serialVersionUID = 1602390434837826147L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private LongValueSpec<ReadOpSpecDefault> atTime;
    private final List<ColSpecRead<ReadOpSpecDefault>> withColumn;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public LongValueSpec<ReadOpSpecDefault> atTime() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.atTime);
        this.atTime = new LongValueSpec<>(this);
        return this.atTime;
    }
    
    @Override
    public RowSpec<ReadOpSpecDefault> from() throws IllegalArgumentException, IllegalStateException {
        final RowSpec<ReadOpSpecDefault> rowSpec;
        rowSpec = new RowSpec<>(this);
        setTableRow(rowSpec);
        return rowSpec;
    }
    
    @Override
    public ColSpecRead<ReadOpSpecDefault> with() throws IllegalStateException {
        final ColSpecRead<ReadOpSpecDefault> withCol;
        prepMutation();
        withCol = new ColSpecRead<>(this);
        this.withColumn.add(withCol);
        return withCol;
    }
    
    @Override
    public <X> ReadOpSpecDefault withAllOf(final Iterable<X> sourceData, final BiConsumer<X, ColSpecReadFluid<?>> dataToColumnGenerator) {
        ColSpecRead<ReadOpSpecDefault> withCol;
        prepMutation();
        if (sourceData != null) {
            for (X element : sourceData) {
                withCol = new ColSpecRead<>(this);
                dataToColumnGenerator.accept(element, new ColSpecReadConfined(withCol));
                this.withColumn.add(withCol);
            }
        }
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public LongValueSpec<ReadOpSpecDefault> getAtTime() {
        return this.atTime;
    }
    @Override
    public List<ColSpecRead<ReadOpSpecDefault>> getWithColumn() {
        return Collections.unmodifiableList(this.withColumn);
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    protected ReadOpSpecDefault self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getTableRow(), this, "from", RowSpec.class);
        Util.validateAtLeastOne(getWithColumn(), this, "with", ColSpecRead.class);
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:READ]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        final RowSpec<ReadOpSpecDefault> tableRow;
        tableRow = getTableRow();
        
        if (format == StringRepFormat.STRUCTURED) {
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
                for (ColSpecRead<ReadOpSpecDefault> colSpec : this.withColumn) {
                    Util.appendIndented(strGen, getDepth() + 1, colSpec);
                }
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (tableRow != null) {
                Util.append(strGen, "from=", tableRow);
                if ((this.atTime != null) && (this.withColumn.size() > 0)) {
                    strGen.append(",");
                }
            }
            if (this.atTime != null) {
                Util.append(strGen, "@ts=", this.atTime);
                if (this.withColumn.size() > 0) {
                    strGen.append(",");
                }
            }
            if (this.withColumn.size() > 0) {
                Util.append(strGen, "col=", this.withColumn);
            }
            strGen.append("}");
        }
    }
    
    @Override
    protected boolean deepEquals(final OperationSpec<?> otherOpSpec) {
        final ReadOpSpecDefault otherReadSpec;
        if (otherOpSpec instanceof ReadOpSpecDefault) {
            otherReadSpec = (ReadOpSpecDefault) otherOpSpec;
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
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ReadOpSpecDefault(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(handle, context, parent);
        this.withColumn = new LinkedList<>();
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
