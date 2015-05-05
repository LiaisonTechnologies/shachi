package com.liaison.hbase.model;

import com.liaison.hbase.util.Util;

public abstract class NamedEntity {
    
    private final Name name;
    
    private String strRep;
    
    public Name getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(getClass().getSimpleName());
            strGen.append(":");
            strGen.append(this.name);
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    protected NamedEntity(final Name name) throws IllegalArgumentException {
        Util.ensureNotNull(name, this, "name", Name.class);
        this.name = name;
        this.strRep = null;
    }
}
