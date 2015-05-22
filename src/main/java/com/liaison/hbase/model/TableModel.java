package com.liaison.hbase.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.liaison.hbase.util.Util;

public final class TableModel extends NamedEntity {

    private static final long serialVersionUID = 5725568314447294526L;

    public static final class Builder {
        private Name name;
        private LinkedHashMap<Name, FamilyModel> families;
        
        public Builder name(final Name name) {
            this.name = name;
            return this;
        }
        public Builder family(final FamilyModel family) throws IllegalArgumentException {
            Util.ensureNotNull(family, this, "family", FamilyModel.class);
            this.families.put(family.getName(), family);
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
    
    public Map<Name, FamilyModel> getFamilies() {
        return this.families;
    }
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
    protected boolean deepEquals(final NamedEntity otherNE) {
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
    }
}
