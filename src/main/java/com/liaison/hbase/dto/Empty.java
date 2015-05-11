package com.liaison.hbase.dto;


public class Empty extends NullableValue {
    
    private static final long serialVersionUID = 4938091499596277118L;
    
    private static final String STR_REP = "--empty--";
    
    private String strRep;
    
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public boolean equals(final Object otherObj) {
        return (otherObj instanceof Empty);
    }
    @Override
    public String toString() {
        if (this.strRep == null) {
            this.strRep = buildStrRep((strGen) -> { strGen.append(STR_REP); });
        }
        return this.strRep;
    }
    
    public Empty(AbstractValueBuilder<?, ?> build) throws IllegalArgumentException {
        super(build);
        if (build.value != null) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                                               + " may only be constructed with a null "
                                               + byte[].class
                                               + " internal value");
        }
    }
}
