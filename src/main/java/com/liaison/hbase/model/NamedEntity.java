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
    
    protected abstract void deepToString(final StringBuilder strGen);
    protected String getEntityTitle() {
        return getClass().getSimpleName();
    }
    @Override
    public final String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(getEntityTitle());
            strGen.append(":");
            strGen.append(this.name);
            deepToString(strGen);
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    protected abstract int deepHashCode();
    @Override
    public final int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(this.name.hashCode() ^ deepHashCode());
        }
        return this.hc.intValue();
    }
    
    protected abstract boolean deepEquals(final NamedEntity otherNE);
    @Override
    public final boolean equals(final Object otherObj) {
        final NamedEntity otherNE;
        if (otherObj instanceof NamedEntity) {
            otherNE = (NamedEntity) otherObj;
            return (Util.refEquals(this.name, otherNE.name) && deepEquals(otherNE));
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
