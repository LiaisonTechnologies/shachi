package com.liaison.hbase.dto;

import com.liaison.commons.Util;

import java.io.Serializable;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.07 12:45
 */
public class CellDatum implements Serializable {

    private static final long serialVersionUID = -6785879802070730317L;

    public static class Builder {

        private Datum datum;
        private TableRow tableRow;
        private FamilyQualifierPair tableColumn;

        public Builder datum(final Datum datum) {
            this.datum = datum;
            return this;
        }
        public Builder row(final TableRow tableRow) {
            this.tableRow = tableRow;
            return this;
        }
        public Builder column(final FamilyQualifierPair tableColumn) {
            this.tableColumn = tableColumn;
            return this;
        }

        public CellDatum build() {
            return new CellDatum(this);
        }
        private Builder() {
            this.datum = null;
            this.tableRow = null;
            this.tableColumn = null;
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    private final Datum datum;
    private final TableRow tableRow;
    private final FamilyQualifierPair tableColumn;

    private Integer hc;
    private String strRep;

    public Datum getDatum() {
        return this.datum;
    }
    public TableRow getTableRow() {
        return this.tableRow;
    }
    public FamilyQualifierPair getTableColumn() {
        return this.tableColumn;
    }

    @Override
    public int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = this.datum.hashCode();
            hCode ^= this.tableRow.hashCode();
            hCode ^= this.tableColumn.hashCode();
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }

    @Override
    public boolean equals(final Object otherObj) {
        final CellDatum otherCD;
        if (this == otherObj) {
            return true;
        } else if (otherObj instanceof CellDatum) {
            otherCD = (CellDatum) otherObj;
            return ((Util.refEquals(this.datum, otherCD.datum))
                    && (Util.refEquals(this.tableRow, otherCD.tableRow))
                    && (Util.refEquals(this.tableColumn, otherCD.tableColumn)));
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(CellDatum.class.getSimpleName());
            strGen.append("(datum=");
            strGen.append(this.datum);
            strGen.append(",row=");
            strGen.append(this.tableRow);
            strGen.append(",column=");
            strGen.append(this.tableColumn);
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private CellDatum(final Builder build) {
        Util.ensureNotNull(build, this, "build", Builder.class);
        Util.ensureNotNull(build.datum, this, "build.datum", Datum.class);
        Util.ensureNotNull(build.tableRow, this, "build.tableRow", TableRow.class);
        Util.ensureNotNull(build.tableColumn,
                           this,
                           "build.tableColumn",
                           FamilyQualifierPair.class);
        this.datum = build.datum;
        this.tableRow = build.tableRow;
        this.tableColumn = build.tableColumn;
    }
}
