package com.liaison.hbase.model;

public class QualModel extends NamedEntity {
    
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
    
    // TODO: equals/hashCode
    
    private QualModel(final Builder build) throws IllegalArgumentException {
        super(build.name);
    }
}
