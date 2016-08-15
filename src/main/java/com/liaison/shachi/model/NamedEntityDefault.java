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
