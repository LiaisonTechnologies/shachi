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
import com.liaison.hbase.model.ser.CellDeserializer;
import com.liaison.hbase.model.ser.CellSerializer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TableModel extends NamedEntityDefault implements TableHB {

    private static final long serialVersionUID = 5725568314447294526L;

    public static final class Builder {
        private Name name;
        private LinkedHashMap<Name, FamilyModel> families;
        private CellSerializer serializer;
        private CellDeserializer deserializer;
        
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
        
        public TableModel build() {
            return new TableModel(this);
        }
        private Builder() {
            this.name = null;
            this.families = new LinkedHashMap<>();
        }
    }
    
    private static final String ENTITY_TITLE = "[TABLE]";
    
    public static Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static TableModel of(final Name name) {
        return with(name).build();
    }
    
    private final Map<Name, FamilyModel> families;
    private final CellSerializer serializer;
    private final CellDeserializer deserializer;

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
    }
}
