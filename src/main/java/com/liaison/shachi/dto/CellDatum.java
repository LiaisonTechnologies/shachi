/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.exception.CellDeserializationException;
import com.liaison.shachi.model.ser.CellDeserializer;

import java.io.Serializable;

/**
 * Data object representing a value in HBase coupled with the {@link TableRow} (table + row) and
 * {@link FamilyQualifierPair} (column family + column qualifier) from which it was derived.
 * <br /><br />
 * If the schema for the read operation which produced this entity included a custom deserializer
 * to convert the raw bytes content of the cell to an object, then
 *
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.07 12:45
 */
public class CellDatum implements Serializable {

    private static final long serialVersionUID = -6785879802070730317L;

    public static class Builder {

        private Datum datum;
        private TableRow tableRow;
        private FamilyQualifierPair tableColumn;
        private CellDeserializer deserializer;

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
        public Builder deserializer(final CellDeserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public CellDatum build() {
            return new CellDatum(this);
        }
        private Builder() {
            this.datum = null;
            this.tableRow = null;
            this.tableColumn = null;
            this.deserializer = null;
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    private final Datum datum;
    private final TableRow tableRow;
    /**
     * The column in the HBase table from which the given value was retrieved. Note that in the
     * case where the original column model may translate to a range (ColumnRange) of different
     * literal values for the column name, this FamilyQualifierPair represents the family and
     * qualifier as the LITERAL strings stored in the database, not as the symbolic string from
     * the original FamilyModel and QualModel used to execute the query.
     */
    private final FamilyQualifierPair tableColumn;
    private final CellDeserializer deserializer;

    private Object content;

    private Integer hc;
    private String strRep;

    public Datum getDatum() {
        return this.datum;
    }
    public TableRow getTableRow() {
        return this.tableRow;
    }

    /**
     * The column in the HBase table from which the given value was retrieved. Note that in the
     * case where the original column model may translate to a range (ColumnRange) of different
     * literal values for the column name, this FamilyQualifierPair represents the family and
     * qualifier as the LITERAL strings stored in the database, not as the symbolic string from
     * the original FamilyModel and QualModel used to execute the query.
     * @return Source column location (family + qualifier
     */
    public FamilyQualifierPair getTableColumn() {
        return this.tableColumn;
    }

    /**
     * Obtain the content of the enclosed Datum, deserialized using the deserizalizer defined at
     * the most specific level (i.e. qualifier level, if a deserializer is defined in the schema
     * at qualifier level, otherwise at the family level, if a deserializer is defined there).
     * @return Contents of the cell, deserialized to a custom object as specified by the source
     * schema.
     * @throws CellDeserializationException If the deserializer specified by the schema cannot
     * decode/interpret the byte contents of the cell
     * @throws IllegalStateException if invoked on a CellDatum whose corresponding table->family->
     * qualifier model tree does not define any deserializers
     */
    public Object getContent() throws CellDeserializationException, IllegalStateException {
        String logMsg;
        if (this.content == null) {
            if (this.deserializer == null) {
                logMsg = "No deserializer is defined for field ("
                         + toString()
                         + ")";
                throw new IllegalStateException(logMsg);
            } else {
                this.content =
                    deserializer.deserialize(this.datum.getValue(DefensiveCopyStrategy.ALWAYS));
            }
        }
        return this.content;
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
        this.deserializer = build.deserializer;
    }
}
