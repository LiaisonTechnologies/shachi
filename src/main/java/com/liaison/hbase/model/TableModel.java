package com.liaison.hbase.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.liaison.hbase.util.Util;

public class TableModel {
    
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
    
    public static Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static TableModel of(final Name name) {
        return with(name).build();
    }
    
    private final Name name;
    private final Map<Name, FamilyModel> families;
    
    public Name getName() {
        return this.name;
    }
    public Map<Name, FamilyModel> getFamilies() {
        return this.families;
    }
    
    // TODO equals, hashCode, toString
    
    private TableModel(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.name, this, "name", Name.class);
        this.name = build.name;
        this.families = Collections.unmodifiableMap(build.families);
    }
}
