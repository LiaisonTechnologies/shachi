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
import com.liaison.shachi.model.ser.CellDeserializer;
import com.liaison.shachi.model.ser.CellSerializer;

public final class QualModel extends NamedEntityDefault implements QualHB {
    
    private static final long serialVersionUID = 7491884927269731635L;

    public static final class Builder {
        private Name name;
        private VersioningModel versioning;
        private CellSerializer serializer;
        private CellDeserializer deserializer;

        public Builder versionWith(final VersioningModel verModel) throws IllegalArgumentException {
            Util.ensureNotNull(verModel, this, "verModel", VersioningModel.class);
            this.versioning = verModel;
            return this;
        }

        public Builder name(final Name name) {
            this.name = name;
            return this;
        }
        public Builder serializer(final CellSerializer serializer) {
            this.serializer = serializer;
            return this;
        }
        public Builder deserializer(final CellDeserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public QualModel build() {
            return new QualModel(this);
        }
        private Builder() {
            this.name = null;
            this.versioning = null;
        }
    }
    
    private static final String ENTITY_TITLE = "[QUAL]";
    
    public static final Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static final QualModel of(final Name name) {
        return with(name).build();
    }

    private final VersioningModel versioning;
    private final CellSerializer serializer;
    private final CellDeserializer deserializer;

    @Override
    public CellSerializer getSerializer() {
        return this.serializer;
    }
    @Override
    public CellDeserializer getDeserializer() {
        return this.deserializer;
    }
    @Override
    protected String getEntityTitle() {
        return ENTITY_TITLE;
    }

    @Override
    public VersioningModel getVersioning() {
        return this.versioning;
    }
    
    @Override
    protected void deepToString(final StringBuilder strGen) {
        // nothing to add
    }
    
    @Override
    protected int deepHashCode() {
        // no need to modify NamedEntityDefault#hashCode
        return 0;
    }

    @Override
    protected boolean deepEquals(final NamedEntityDefault otherNE) {
        // beyond ensuring that it is a QualModel instance, no need to modify NamedEntityDefault#equals
        return (otherNE instanceof QualModel);
    }
    
    private QualModel(final Builder build) throws IllegalArgumentException {
        super(build.name);

        this.versioning = build.versioning;

        this.serializer = build.serializer;
        this.deserializer = build.deserializer;
    }
}
