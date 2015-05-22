package com.liaison.hbase.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.liaison.hbase.util.Util;

public final class FamilyModel extends NamedEntity {
    
    private static final long serialVersionUID = -6051047393328804323L;

    public static final class Builder {
        private Name name;
        private LinkedHashMap<Name, QualModel> quals;
        private boolean closedQualSet;
        
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
    
    public Map<Name, QualModel> getQuals() {
        return this.quals;
    }
    public boolean isClosedQualSet() {
        return closedQualSet;
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
        this.quals = Collections.unmodifiableMap(build.quals);
        this.closedQualSet = build.closedQualSet;
    }
}
