package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.model.TableModel;

import java.io.Serializable;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.06 13:38
 */
public class TableRow implements RowRef, Serializable {

    public static final class Builder {
        private TableModel table;
        private RowKey row;
        private String description;

        public Builder table(final TableModel table) {
            this.table = table;
            return this;
        }
        public Builder row(final RowKey row) {
            this.row = row;
            return this;
        }
        public Builder description(final String description) {
            this.description = description;
            return this;
        }
        public TableRow build() throws IllegalArgumentException {
            return new TableRow(this);
        }
        private Builder() {
            this.table = null;
            this.row = null;
            this.description = null;
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }
    public static TableRow of(final TableModel table, final RowKey row, final String description) throws IllegalArgumentException {
        return getBuilder().table(table).row(row).description(description).build();
    }
    public static TableRow of(final TableModel table, final RowKey row) throws IllegalArgumentException {
        return getBuilder().table(table).row(row).build();
    }

    private final TableModel table;
    private final RowKey row;
    /**
     * description is an optional field, so it is important that it NOT be included in hashCode
     * and equals implementations, so that instances with and without a description (or with
     * differing descriptions) are judged equal when used as map keys, etc.
     */
    private final String description;

    private Integer hc;
    private String strRep;

    @Override
    public TableModel getTable() {
        return null;
    }
    @Override
    public RowKey getRowKey() {
        return null;
    }
    public String getDescription() {
        return this.description;
    }

    public int hashCode() {
        int hcInt;
        if (this.hc == null) {
            hcInt = this.table.hashCode();
            hcInt ^= this.row.hashCode();
            this.hc = Integer.valueOf(hcInt);
        }
        return this.hc.intValue();
    }
    public boolean equals(final Object otherObj) {
        final TableRow otherTR;
        if (otherObj instanceof TableRow) {
            otherTR = (TableRow) otherObj;
            return (Util.refEquals(this.table, otherTR.table)
                && Util.refEquals(this.row, otherTR.row));
        }
        return false;
    }
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(TableRow.class.getSimpleName());
            strGen.append("(table=");
            strGen.append(this.table);
            strGen.append(",row=");
            strGen.append(this.row);
            if (this.description != null) {
                strGen.append(",description='");
                strGen.append(this.description);
                strGen.append("'");
            }
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private TableRow(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.table, this, "table", TableModel.class);
        Util.ensureNotNull(build.row, this, "column", RowKey.class);
        this.table = build.table;
        this.row = build.row;
        this.description = build.description;
    }
}
