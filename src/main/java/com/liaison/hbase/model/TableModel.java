/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.model;

import com.liaison.commons.Util;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.ser.CellDeserializer;
import com.liaison.hbase.model.ser.CellSerializer;
import com.liaison.hbase.util.HBaseUtil;
import com.liaison.serialization.BytesUtil;
import com.liaison.serialization.DefensiveCopyStrategy;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class TableModel extends NamedEntityDefault implements TableHB {

    private static final long serialVersionUID = 5725568314447294526L;

    public static final class Builder {
        private Name name;
        private LinkedHashMap<Name, FamilyModel> families;
        private CellSerializer serializer;
        private CellDeserializer deserializer;
        private Function<RowKey, byte[]> rowKeyLiteralizer;
        
        public Builder name(final Name name) {
            this.name = name;
            return this;
        }
        public Builder family(final FamilyModel family) throws IllegalArgumentException {
            String excMsg;
            final Name familyName;

            Util.ensureNotNull(family, this, "family", FamilyModel.class);

            familyName = family.getName();
            if (this.families.containsKey(familyName)) {
                excMsg =
                    "Duplicate column family "
                    + familyName.toString()
                    + " specified for table "
                    + this.name;
                throw new IllegalArgumentException(excMsg);
            }
            this.families.put(family.getName(), family);
            return this;
        }
        public Builder serializer(final CellSerializer serializer) {
            this.serializer = serializer;
            return this;
        }
        public Builder deserializer(final CellDeserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }
        public Builder saltRows(final Function<RowKey, byte[]> rowKeyLiteralizer) {
            this.rowKeyLiteralizer = rowKeyLiteralizer;
            return this;
        }
        public Builder saltRows() {
            return saltRows(SALTING_ROWKEY_LITERALIZER_DEFAULT);
        }
        
        public TableModel build() {
            return new TableModel(this);
        }
        private Builder() {
            this.name = null;
            this.families = new LinkedHashMap<>();
        }
    }
    
    private static final String ENTITY_TITLE = "[TABLE]";

    private static final Function<RowKey, byte[]> SALTING_ROWKEY_LITERALIZER_DEFAULT =
        HBaseUtil::saltRowKeyMurmur3_32;

    public static byte[] literalizeRowKey(final RowKey rk) {
        return rk.getValue(DefensiveCopyStrategy.ALWAYS);
    }

    public static Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static TableModel of(final Name name) {
        return with(name).build();
    }
    
    private final Map<Name, FamilyModel> families;
    private final CellSerializer serializer;
    private final CellDeserializer deserializer;
    private final Function<RowKey, byte[]> rowKeyLiteralizer;

    @Override
    public CellSerializer getSerializer() {
        return this.serializer;
    }
    @Override
    public CellDeserializer getDeserializer() {
        return this.deserializer;
    }
    @Override
    public Map<Name, FamilyModel> getFamilies() {
        return this.families;
    }
    @Override
    public FamilyModel getFamily(final Name famName) {
        return this.families.get(famName);
    }

    public byte[] literalize(final RowKey rk) {
        byte[] literalRK;

        literalRK = this.rowKeyLiteralizer.apply(rk);
        if (literalRK == null) {
            literalRK = BytesUtil.HBASE_EMPTY;
        }
        return literalRK;
    }

    @Override
    protected String getEntityTitle() {
        return ENTITY_TITLE;
    }
    
    @Override
    protected void deepToString(final StringBuilder strGen) {
        /*
        strGen.append(":fam={");
        strGen.append(this.families);
        strGen.append("}");
        */
    }
    
    @Override
    protected int deepHashCode() {
        return this.families.hashCode();
    }
    
    @Override
    protected boolean deepEquals(final NamedEntityDefault otherNE) {
        final TableModel otherTableModel;
        if (otherNE instanceof TableModel) {
            otherTableModel = (TableModel) otherNE;
            return this.families.equals(otherTableModel.families);
        }
        return false;
    }
    
    private TableModel(final Builder build) throws IllegalArgumentException {
        super(build.name);
        this.families = Collections.unmodifiableMap(build.families);
        this.serializer = build.serializer;
        this.deserializer = build.deserializer;
        if (build.rowKeyLiteralizer == null) {
            this.rowKeyLiteralizer = TableModel::literalizeRowKey;
        } else {
            this.rowKeyLiteralizer = build.rowKeyLiteralizer;
        }
    }
}
