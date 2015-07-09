/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.frozen.ColSpecReadFrozen;
import com.liaison.hbase.exception.HBaseException;

import java.io.Serializable;

public class SingleCellResult extends CellResult<CellDatum> implements Serializable {

    public static final class Builder {
        private HBaseException exc;
        private CellDatum datum;
        private TableRow tableRow;
        private FamilyQualifierPair tableColumn;
        private ColSpecReadFrozen spec;

        public Builder exc(final HBaseException exc) {
            this.exc = exc;
            return this;
        }
        public Builder datum(final CellDatum datum) {
            this.datum = datum;
            return this;
        }
        public Builder tableRow(final TableRow tableRow) {
            this.tableRow = tableRow;
            return this;
        }
        public Builder tableColumn(final FamilyQualifierPair tableColumn) {
            this.tableColumn = tableColumn;
            return this;
        }
        public Builder spec(final ColSpecReadFrozen spec) {
            this.spec = spec;
            return this;
        }
        public SingleCellResult build() throws IllegalArgumentException {
            String logMsg;

            if ((this.datum != null) && (this.exc != null)) {
                logMsg =
                    SingleCellResult.class.getSimpleName()
                    + " cannot define both data ("
                    + CellDatum.class.getName()
                    + ") and an exception ("
                    + HBaseException.class.getName()
                    + "); exactly 1 must be defined";
                throw new IllegalArgumentException(logMsg);
            }
            if (this.datum != null) {
                return new SingleCellResult(this, this.datum);
            } else if (this.exc != null) {
                return new SingleCellResult(this, this.exc);
            } else {
                logMsg =
                    SingleCellResult.class.getSimpleName()
                    + " must define exactly 1 of: datum ("
                    + CellDatum.class.getName()
                    + ") OR exception ("
                    + HBaseException.class.getName()
                    + "); existing builder has neither";
                throw new IllegalArgumentException(logMsg);
            }
        }
    }

    private static final long serialVersionUID = 5186988866264811933L;

    private static void validateBuilder(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build, SingleCellResult.class.getSimpleName(), "build", Builder.class);
        Util.ensureNotNull(build.tableRow,
                           SingleCellResult.class.getSimpleName(),
                           "tableRow",
                           TableRow.class);
        // note: spec is optional
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    private final TableRow tableRow;
    private final FamilyQualifierPair tableColumn;
    private final ColSpecReadFrozen spec;

    public TableRow getTableRow() {
        return this.tableRow;
    }
    public FamilyQualifierPair getTableColumn() {
        return this.tableColumn;
    }
    public ColSpecReadFrozen getSpec() {
        return this.spec;
    }

    @Override
    public int hashCodeSubord() {
        // note: spec not included in hashCode
        return Util.hashCode(this.tableRow) ^ Util.hashCode(this.tableColumn);
    }

    @Override
    public boolean equalsSubord(final CellResult otherCR) {
        SingleCellResult otherSCR;
        if (otherCR instanceof SingleCellResult) {
            otherSCR = (SingleCellResult) otherCR;
            // note: spec not included in equals
            return (Util.refEquals(this.tableRow, otherSCR.tableRow)
                    && Util.refEquals(this.tableColumn, otherSCR.tableColumn));
        }
        return false;
    }

    @Override
    public void toStringSubord(final StringBuilder strGen) {
        strGen.append("table+row=");
        strGen.append(this.tableRow);
        strGen.append(",column=");
        strGen.append(this.tableColumn);
        if (this.spec != null) {
            strGen.append(",spec.handle='");
            strGen.append(this.spec.getHandle());
            strGen.append("'");
        }
    }

    private SingleCellResult(final Builder build, final HBaseException exc) throws IllegalArgumentException {
        super(exc);
        validateBuilder(build);
        this.tableRow = build.tableRow;
        this.tableColumn = build.tableColumn;
        this.spec = build.spec;
    }
    private SingleCellResult(final Builder build, final CellDatum datum) throws IllegalArgumentException {
        super(datum);
        validateBuilder(build);
        this.tableRow = build.tableRow;
        this.tableColumn = build.tableColumn;
        this.spec = build.spec;
    }
}
