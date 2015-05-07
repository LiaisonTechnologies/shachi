package com.liaison.hbase.model;

import java.io.Serializable;

import com.liaison.hbase.util.Util;

public abstract class NamedEntity implements Serializable {
    
    private static final long serialVersionUID = 8555269582123629937L;

    private final Name name;
    
    private String strRep;
    private Integer hc;
    
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
    
    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(this.name.hashCode());
        }
        return this.hc.intValue();
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        final NamedEntity otherNE;
        if (otherObj instanceof NamedEntity) {
            otherNE = (NamedEntity) otherObj;
            return Util.refEquals(this.name, otherNE.name);
        }
        return false;
    }
    
    protected NamedEntity(final Name name) throws IllegalArgumentException {
        Util.ensureNotNull(name, this, "name", Name.class);
        this.name = name;
        this.strRep = null;
        this.hc = null;
    }
}
