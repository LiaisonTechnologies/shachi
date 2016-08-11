/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.model;

import com.liaison.javabasics.commons.Util;

import java.io.Serializable;

public abstract class NamedEntityDefault implements NamedEntity, Serializable {
    
    private static final long serialVersionUID = 8555269582123629937L;

    private final Name name;
    
    private String strRep;
    private Integer hc;

    @Override
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
    
    protected abstract boolean deepEquals(final NamedEntityDefault otherNE);
    
    @Override
    public final boolean equals(final Object otherObj) {
        final NamedEntityDefault otherNE;
        if (otherObj instanceof NamedEntityDefault) {
            otherNE = (NamedEntityDefault) otherObj;
            return (Util.refEquals(this.name, otherNE.name) && deepEquals(otherNE));
        }
        return false;
    }
    
    protected NamedEntityDefault(final Name name) throws IllegalArgumentException {
        Util.ensureNotNull(name, this, "name", Name.class);
        this.name = name;
        this.strRep = null;
        this.hc = null;
    }
}
