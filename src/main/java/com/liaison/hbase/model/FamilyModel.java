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
import com.liaison.hbase.model.ser.CellDeserializer;
import com.liaison.hbase.model.ser.CellSerializer;
import com.liaison.serialization.BytesUtil;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FamilyModel extends NamedEntityDefault implements FamilyHB {
    
    private static final long serialVersionUID = -6051047393328804323L;

    public static final class Builder {
        private Name name;
        private final LinkedHashMap<Name, QualModel> quals;
        private final Map<QualHB, CellSerializer> qualSerializers;
        private final Map<QualHB, CellDeserializer> qualDeserializers;
        private boolean closedQualSet;
        private EnumSet<VersioningModel> versioning;
        private CellSerializer serializer;
        private CellDeserializer deserializer;

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
            String excMsg;
            final Name qualName;
            final CellSerializer cellSer;
            final CellDeserializer cellDeser;

            Util.ensureNotNull(qual, this, "column", QualModel.class);

            qualName = qual.getName();
            if (this.quals.containsKey(qualName)) {
                excMsg =
                    "Duplicate column qualifier "
                    + qualName.toString()
                    + " specified for family "
                    + this.name;
                throw new IllegalArgumentException(excMsg);
            }
            this.quals.put(qualName, qual);

            cellSer = qual.getSerializer();
            cellDeser = qual.getDeserializer();
            if (cellSer != null) {
                this.qualSerializers.put(qual, cellSer);
            }
            if (cellDeser != null) {
                this.qualDeserializers.put(qual, cellDeser);
            }

            return this;
        }
        public Builder closedQualSet(final boolean closedQualSet) {
            this.closedQualSet = closedQualSet;
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
        
        public FamilyModel build() {
            return new FamilyModel(this);
        }
        private Builder() {
            this.closedQualSet = false;
            this.name = null;
            this.quals = new LinkedHashMap<>();
            this.qualSerializers = new HashMap<>();
            this.qualDeserializers = new HashMap<>();
            this.versioning = EnumSet.noneOf(VersioningModel.class);
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
    private final Map<QualHB, CellSerializer> qualSerializers;
    private final Map<QualHB, CellDeserializer> qualDeserializers;
    private final boolean closedQualSet;
    private final EnumSet<VersioningModel> versioning;
    private final CellSerializer serializer;
    private final CellDeserializer deserializer;

    private <S> S getSerializationComponentDeferToQual(final QualHB forQualInstance, final Map<QualHB, S> qualSerComponentMap, final S defaultComponentForFamily) {
        S serComponent;
        serComponent = qualSerComponentMap.get(forQualInstance);
        if (serComponent == null) {
            serComponent = defaultComponentForFamily;
        }
        return serComponent;
    }
    public CellSerializer getSerializer(final QualHB forQualInstance) {
        return getSerializationComponentDeferToQual(forQualInstance,
                                                    this.qualSerializers,
                                                    this.serializer);
    }
    public CellDeserializer getDeserializer(final QualHB forQualInstance) {
        return getSerializationComponentDeferToQual(forQualInstance,
                                                    this.qualDeserializers,
                                                    this.deserializer);
    }

    @Override
    public CellSerializer getSerializer() {
        return this.serializer;
    }
    @Override
    public CellDeserializer getDeserializer() {
        return this.deserializer;
    }
    @Override
    public Map<Name, QualModel> getQuals() {
        return this.quals;
    }
    @Override
    public boolean isClosedQualSet() {
        return closedQualSet;
    }
    @Override
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
        strGen.append(":column={");
        strGen.append(this.quals);
        strGen.append("}");
        */
    }
    
    @Override
    protected int deepHashCode() {
        return this.quals.hashCode();
    }
    
    @Override
    protected boolean deepEquals(final NamedEntityDefault otherNE) {
        /*
         * TODO: TEMPORARY FIX!
         * This is a temporary fix to resolve the problem where the result-parsing logic cannot
         * match a result from HBase with the original model, because the comparison of family and
         * qualifier column identifiers does not match. The identifying family/column model instances
         * coming from HBase include only the name, and not additional meta-data, so performing a
         * "deep" equals check with the original model returns false, so the assimilation process
         * is never able to perform a match.
         *
         * The permanent fix will abstract the name away into a separate reference class, so the
         * model and the identifier determined from the DB can be easily matched based on name
         * alone.
         */
        return true;
        /*
        final FamilyModel otherFamilyModel;
        if (otherNE instanceof FamilyModel) {
            otherFamilyModel = (FamilyModel) otherNE;
            return ((this.closedQualSet == otherFamilyModel.closedQualSet)
                    && (this.quals.equals(otherFamilyModel.quals)));
        }
        return false;
        */
    }
    
    private FamilyModel(final Builder build) throws IllegalArgumentException {
        super(build.name);

        this.closedQualSet = build.closedQualSet;

        Util.ensureNotNull(build.quals, this, "quals", LinkedHashMap.class);
        this.quals = Collections.unmodifiableMap(build.quals);
        Util.ensureNotNull(build.qualSerializers, this, "qualSerializers", HashMap.class);
        this.qualSerializers = Collections.unmodifiableMap(build.qualSerializers);
        Util.ensureNotNull(build.qualDeserializers, this, "qualDeserializers", HashMap.class);
        this.qualDeserializers = Collections.unmodifiableMap(build.qualDeserializers);

        /*
        versioning should never be null, even if no versioning scheme is enabled; in that case, the
        versioning variable should be EnumSet.noneOf(VersioningModel.class)
         */
        Util.ensureNotNull(build.versioning, this, "versioning", EnumSet.class);
        this.versioning = build.versioning;

        this.serializer = build.serializer;
        this.deserializer = build.deserializer;
    }
}
