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
package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.model.QualModel;

import java.io.Serializable;

public class FamilyQualifierPair implements ColRef, Serializable {

    private static final long serialVersionUID = -3126811569021715389L;

    public static final class Builder {
        private FamilyHB family;
        private QualHB column;
        private String description;
        private boolean optional;
        
        public Builder family(final FamilyHB family) {
            this.family = family;
            return this;
        }
        public Builder column(final QualHB column) {
            this.column = column;
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
            this.column = null;
            this.description = null;
            this.optional = false;
        }
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    public static FamilyQualifierPair of(final FamilyModel fam, final QualModel qual, final String description) throws IllegalArgumentException {
        return getBuilder().family(fam).column(qual).description(description).build();
    }
    public static FamilyQualifierPair of(final FamilyHB fam, final QualHB qual) throws IllegalArgumentException {
        return getBuilder().family(fam).column(qual).build();
    }
    
    private final FamilyHB family;
    private final QualHB column;
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

    @Override
    public FamilyHB getFamily() {
        return this.family;
    }
    @Override
    public QualHB getColumn() {
        return this.column;
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
            hcInt ^= this.column.hashCode();
            this.hc = Integer.valueOf(hcInt);
        }
        return this.hc.intValue();
    }
    public boolean equals(final Object otherObj) {
        final FamilyQualifierPair otherFQP;
        if (this == otherObj) {
            return true;
        }
        if (otherObj instanceof FamilyQualifierPair) {
            otherFQP = (FamilyQualifierPair) otherObj;
            return (Util.refEquals(this.family, otherFQP.family)
                    && Util.refEquals(this.column, otherFQP.column));
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
            strGen.append(",column=");
            strGen.append(this.column);
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
        Util.ensureNotNull(build.column, this, "column", QualModel.class);
        this.family = build.family;
        this.column = build.column;
        this.description = build.description;
        this.optional = build.optional;
    }
}
