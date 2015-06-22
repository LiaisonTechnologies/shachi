/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;

import java.io.Serializable;

public class FamilyQualifierPair implements Serializable {

    private static final long serialVersionUID = -3126811569021715389L;

    public static final class Builder {
        private FamilyModel family;
        private QualModel qual;
        private String description;
        private boolean optional;
        
        public Builder family(final FamilyModel family) {
            this.family = family;
            return this;
        }
        public Builder qual(final QualModel qual) {
            this.qual = qual;
            return this;
        }
        public Builder description(final String description) {
            this.description = description;
            return this;
        }
        public Builder optional(final boolean optional) {
            this.optional = optional;
            return this;
        }
        public FamilyQualifierPair build() throws IllegalArgumentException {
            return new FamilyQualifierPair(this);
        }
        private Builder() {
            this.family = null;
            this.qual = null;
            this.description = null;
            this.optional = false;
        }
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    public static FamilyQualifierPair of(final FamilyModel fam, final QualModel qual, final String description) throws IllegalArgumentException {
        return getBuilder().family(fam).qual(qual).description(description).build();
    }
    public static FamilyQualifierPair of(final FamilyModel fam, final QualModel qual) throws IllegalArgumentException {
        return getBuilder().family(fam).qual(qual).build();
    }
    
    private final FamilyModel family;
    private final QualModel qual;
    /**
     * description is an optional field, so it is important that it NOT be included in hashCode
     * and equals implementations, so that instances with and without a description (or with
     * differing descriptions) are judged equal when used as map keys, etc.
     */
    private final String description;
    /**
     * optional is an optional field, so it is important that it NOT be included in hashCode
     * and equals implementations, so that instances differing optional flags are judged equal when
     * used as map keys, etc.
     */
    private final boolean optional;

    private Integer hc;
    private String strRep;

    public FamilyModel getFamily() {
        return this.family;
    }
    public QualModel getQual() {
        return this.qual;
    }
    public String getDescription() {
        return this.description;
    }
    public boolean isOptional() {
        return this.optional;
    }
    
    public int hashCode() {
        int hcInt;
        if (this.hc == null) {
            hcInt = this.family.hashCode();
            hcInt ^= this.qual.hashCode();
            this.hc = Integer.valueOf(hcInt);
        }
        return this.hc.intValue();
    }
    public boolean equals(final Object otherObj) {
        final FamilyQualifierPair otherFQP;
        if (otherObj instanceof FamilyQualifierPair) {
            otherFQP = (FamilyQualifierPair) otherObj;
            return (Util.refEquals(this.family, otherFQP.family)
                    && Util.refEquals(this.qual, otherFQP.qual));
        }
        return false;
    }
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(FamilyQualifierPair.class.getSimpleName());
            strGen.append("(family=");
            strGen.append(this.family);
            strGen.append(",qual=");
            strGen.append(this.qual);
            if (this.description != null) {
                strGen.append(",description='");
                strGen.append(this.description);
            }
            strGen.append("'::");
            if (this.optional) {
                strGen.append("OPT");
            } else {
                strGen.append("REQ");
            }
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    private FamilyQualifierPair(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.family, this, "family", FamilyModel.class);
        Util.ensureNotNull(build.qual, this, "qual", QualModel.class);
        this.family = build.family;
        this.qual = build.qual;
        this.description = build.description;
        this.optional = build.optional;
    }
}
