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
import com.liaison.hbase.api.request.fluid.fluent.RowSpecFluent;
import com.liaison.hbase.api.request.frozen.RowSpecFrozen;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.SpecUtil;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P>
 */
public final class RowSpec<P extends OperationSpec<P>> extends CriteriaSpec<RowSpec<P>, P> implements RowSpecFluent<RowSpec<P>, P>, RowSpecFrozen, Serializable {
    
    private static final long serialVersionUID = 1106826447086026044L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private TableModel table;
    private RowKey rowKey;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public RowSpec<P> tbl(final TableModel table) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.table =
            Util.validateExactlyOnceParam(table, this, "table", TableModel.class, this.table);
        return self();
    }
    
    @Override
    public RowSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.rowKey =
            Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    
    @Override
    public TableModel getTable() {
        return this.table;
    }
    
    @Override
    public RowKey getRowKey() {
        return this.rowKey;
    }

    @Override
    public byte[] getLiteralizedRowKeyBytes() throws IllegalStateException {
        prepPostFreezeOp("getLiteralizedRowKeyBytes");
        return this.table.literalize(this.rowKey);
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[@table,@row]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.table != null) {
                Util.appendIndented(strGen, getDepth() + 1, "table: ", this.table, "\n");
            }
            if (this.rowKey != null) {
                Util.appendIndented(strGen, getDepth() + 1, "rowKey: ", this.rowKey, "\n");
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (this.table != null) {
                Util.append(strGen, "table=", this.table);
                if (this.rowKey != null) {
                    strGen.append(",");
                }
            }
            if (this.rowKey != null) {
                Util.append(strGen, "row=", this.rowKey);
            }
            strGen.append("}");
        }
    }

    @Override
    protected int prepareHashCode() {
        return (Util.hashCode(this.table) ^ Util.hashCode(this.rowKey));
    }

    @Override
    public boolean equals(final Object otherObj) {
        final RowSpec<?> otherRowSpec;
        if (this == otherObj) {
            return true;
        }
        if (otherObj instanceof RowSpec) {
            otherRowSpec = (RowSpec<?>) otherObj;
            return (Util.refEquals(this.table, otherRowSpec.table)
                    && Util.refEquals(this.rowKey, otherRowSpec.rowKey));
        }
        return false;
    }
    
    @Override
    public RowSpec<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        SpecUtil.validateRequired(getTable(), this, "tbl", TableModel.class);
        SpecUtil.validateRequired(getRowKey(), this, "row", RowKey.class);
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    public RowSpec(final P parent) {
        super(parent);
    }
}
