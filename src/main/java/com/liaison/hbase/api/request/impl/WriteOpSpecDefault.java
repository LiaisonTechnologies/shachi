/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.WriteOpSpec;
import com.liaison.hbase.api.request.fluid.ColSpecWriteFluid;
import com.liaison.hbase.api.request.frozen.ColSpecWriteFrozen;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.util.SpecUtil;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class WriteOpSpecDefault extends TableRowOpSpec<WriteOpSpecDefault> implements WriteOpSpec<OpResultSet>, Serializable {

    private static final long serialVersionUID = 2572256818666730468L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private CondSpec<WriteOpSpecDefault> givenCondition;
    private final List<ColSpecWrite<WriteOpSpecDefault>> withColumn;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public RowSpec<WriteOpSpecDefault> on() throws IllegalArgumentException, IllegalStateException {
        final RowSpec<WriteOpSpecDefault> rowSpec;
        rowSpec = new RowSpec<>(this);
        setTableRow(rowSpec);
        return rowSpec;
    }
    
    @Override
    public CondSpec<WriteOpSpecDefault> given() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("givenCondition", CondSpec.class, this.givenCondition);
        this.givenCondition = new CondSpec<>(this);
        return this.givenCondition;
    }
    
    @Override
    public ColSpecWrite<WriteOpSpecDefault> with(final Object handle) throws IllegalStateException {
        final ColSpecWrite<WriteOpSpecDefault> withCol;
        prepMutation();
        withCol = new ColSpecWrite<>(this, handle);
        this.withColumn.add(withCol);
        return withCol;
    }
    @Override
    public ColSpecWrite<WriteOpSpecDefault> with() throws IllegalStateException {
        return with(null);
    }

    @Override
    public <X> WriteOpSpecDefault withAllOf(final Iterable<X> sourceData, final BiFunction<X, ColSpecWriteFluid<?>, Object> dataToColumnGenerator) {
        ColSpecWrite<WriteOpSpecDefault> withCol;
        Object handle;

        prepMutation();
        if (sourceData != null) {
            for (X element : sourceData) {
                withCol = new ColSpecWrite<>(this);
                handle = dataToColumnGenerator.apply(element, new ColSpecWriteConfined(withCol));
                withCol.handle(handle);
                this.withColumn.add(withCol);
            }
        }
        return self();
    }

    @Override
    public <X> WriteOpSpecDefault withAllOf(final Iterable<X> sourceData, final BiConsumer<X, ColSpecWriteFluid<?>> dataToColumnGenerator) {
        return withAllOf(sourceData,
                         // convert the BiConsumer to a null-returning BiFunction
                         (X sourceDataElement, ColSpecWriteFluid<?> colSpec) -> {
                             dataToColumnGenerator.accept(sourceDataElement, colSpec);
                             return null;
                         });
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public CondSpec<WriteOpSpecDefault> getGivenCondition() {
        return this.givenCondition;
    }
    
    @Override
    public List<ColSpecWriteFrozen> getWithColumn() {
        return Collections.unmodifiableList(this.withColumn);
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public WriteOpSpecDefault self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        SpecUtil.validateRequired(getTableRow(), this, "from", RowSpec.class);
        SpecUtil.validateAtLeastOne(getWithColumn(), this, "with", ColSpecWrite.class);
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:WRITE]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        final RowSpec<WriteOpSpecDefault> tableRow;
        tableRow = getTableRow();
        if (format == StringRepFormat.STRUCTURED) {
            if (tableRow != null) {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "on table/row: ",
                                    "\n",
                                    tableRow,
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
                for (ColSpecWrite<WriteOpSpecDefault> colSpec : this.withColumn) {
                    Util.appendIndented(strGen, getDepth() + 1, colSpec);
                }
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (tableRow != null) {
                Util.append(strGen, "on=", tableRow);
                if ((this.givenCondition != null) && (this.withColumn.size() > 0)) {
                    strGen.append(",");
                }
            }
            if (this.givenCondition != null) {
                Util.append(strGen, "if=", this.givenCondition);
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
        final WriteOpSpecDefault otherWriteSpec;
        if (otherOpSpec instanceof WriteOpSpecDefault) {
            otherWriteSpec = (WriteOpSpecDefault) otherOpSpec;
            /*
             * Equality checks ordered from least to most expensive, to allow for relatively quick
             * short-circuiting
             */
            return ((Util.refEquals(this.getTableRow(), otherWriteSpec.getTableRow()))
                    &&
                    (Util.refEquals(this.givenCondition, otherWriteSpec.givenCondition))
                    &&
                    (Util.refEquals(this.withColumn, otherWriteSpec.withColumn)));
        }
        return false;
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public WriteOpSpecDefault(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(handle, context, parent);
        this.withColumn = new LinkedList<>();
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
