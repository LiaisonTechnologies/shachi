package com.liaison.hbase.model;

import com.liaison.hbase.util.Util;

public class QualModel {
    
    public static final class Builder {
        private Name name;
        
        public Builder name(final Name name) {
            this.name = name;
            return this;
        }

        public QualModel build() {
            return new QualModel(this);
        }
        private Builder() {
            this.name = null;
        }
    }
    
    public static final Builder with(final Name name) {
        return new Builder().name(name);
    }
    public static final QualModel of(final Name name) {
        return with(name).build();
    }
    
    private final Name name;
    
    public Name getName() {
        return this.name;
    }
    
    private QualModel(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.name, this, "name", Name.class);
        this.name = build.name;
    }
}
