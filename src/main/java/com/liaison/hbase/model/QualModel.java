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

import java.util.EnumSet;

public final class QualModel extends NamedEntity {
    
    private static final long serialVersionUID = 7491884927269731635L;

    public static final class Builder {
        private Name name;
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

        public QualModel build() {
            return new QualModel(this);
        }
        private Builder() {
            this.name = null;
            this.versioning = EnumSet.noneOf(VersioningModel.class);
        }
    }
    
    private static final String ENTITY_TITLE = "[QUAL]";
    
    public static final Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static final QualModel of(final Name name) {
        return with(name).build();
    }

    private final EnumSet<VersioningModel> versioning;
    
    @Override
    protected String getEntityTitle() {
        return ENTITY_TITLE;
    }

    public EnumSet<VersioningModel> getVersioning() {
        return this.versioning;
    }
    
    @Override
    protected void deepToString(final StringBuilder strGen) {
        // nothing to add
    }
    
    @Override
    protected int deepHashCode() {
        // no need to modify NamedEntity#hashCode
        return 0;
    }

    @Override
    protected boolean deepEquals(final NamedEntity otherNE) {
        // beyond ensuring that it is a QualModel instance, no need to modify NamedEntity#equals
        return (otherNE instanceof QualModel);
    }
    
    private QualModel(final Builder build) throws IllegalArgumentException {
        super(build.name);
        /*
        versioning should never be null, even if no versioning scheme is enabled; in that case, the
        versioning variable should be EnumSet.noneOf(VersioningModel.class)
         */
        Util.ensureNotNull(build.versioning, this, "versioning", EnumSet.class);
        this.versioning = build.versioning;
    }
}
