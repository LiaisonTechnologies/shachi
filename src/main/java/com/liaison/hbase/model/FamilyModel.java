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

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FamilyModel extends NamedEntity {
    
    private static final long serialVersionUID = -6051047393328804323L;

    public static final class Builder {
        private Name name;
        private LinkedHashMap<Name, QualModel> quals;
        private boolean closedQualSet;
        private EnumSet<VersioningModel> versioning;

        public Builder versionWith(final VersioningModel verModel) throws IllegalArgumentException {
            Util.ensureNotNull(verModel, this, "verModel", VersioningModel.class);
            this.versioning.add(verModel);
            return this;
        }
        public Builder name(final Name name) {
            this.name = name;
            return this;
        }
        public Builder qual(final QualModel qual) throws IllegalArgumentException {
            Util.ensureNotNull(qual, this, "qual", QualModel.class);
            this.quals.put(qual.getName(), qual);
            return this;
        }
        public Builder closedQualSet(final boolean closedQualSet) {
            this.closedQualSet = closedQualSet;
            return this;
        }
        
        public FamilyModel build() {
            return new FamilyModel(this);
        }
        private Builder() {
            this.closedQualSet = false;
            this.name = null;
            this.quals = new LinkedHashMap<>();
        }
    }
    
    private static final String ENTITY_TITLE = "[FAM]";
    
    public static Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static FamilyModel of(final Name name) {
        return with(name).build();
    }
    
    private final Map<Name, QualModel> quals;
    private final boolean closedQualSet;
    private final EnumSet<VersioningModel> versioning;
    
    public Map<Name, QualModel> getQuals() {
        return this.quals;
    }
    public boolean isClosedQualSet() {
        return closedQualSet;
    }
    public EnumSet<VersioningModel> getVersioning() {
        return this.versioning;
    }
    
    @Override
    protected String getEntityTitle() {
        return ENTITY_TITLE;
    }
    
    @Override
    protected void deepToString(final StringBuilder strGen) {
        /*
        strGen.append(":qual={");
        strGen.append(this.quals);
        strGen.append("}");
        */
    }
    
    @Override
    protected int deepHashCode() {
        return this.quals.hashCode();
    }
    
    @Override
    protected boolean deepEquals(final NamedEntity otherNE) {
        final FamilyModel otherFamilyModel;
        if (otherNE instanceof FamilyModel) {
            otherFamilyModel = (FamilyModel) otherNE;
            return ((this.closedQualSet == otherFamilyModel.closedQualSet)
                    && (this.quals.equals(otherFamilyModel.quals)));
        }
        return false;
    }
    
    private FamilyModel(final Builder build) throws IllegalArgumentException {
        super(build.name);

        this.closedQualSet = build.closedQualSet;

        Util.ensureNotNull(build.quals, this, "quals", LinkedHashMap.class);
        this.quals = Collections.unmodifiableMap(build.quals);

        /*
        versioning should never be null, even if no versioning scheme is enabled; in that case, the
        versioning variable should be EnumSet.noneOf(VersioningModel.class)
         */
        Util.ensureNotNull(build.versioning, this, "versioning", EnumSet.class);
        this.versioning = build.versioning;
    }
}
