package com.liaison.hbase.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.liaison.hbase.util.Util;

public class Name implements Serializable {
    
    private static final long serialVersionUID = -290504940089138756L;

    public static final class Builder {
        private byte[] name;
        private String str;
        private Set<String> alias;
        
        public Builder name(final byte[] name, Charset decoding) {
            this.name = Util.copyOf(name);
            if (decoding == null) {
                this.str = Util.toString(name);
            } else {
                this.str = Util.toString(name, decoding);
            }
            return this;
        }
        public Builder name(final byte[] name) {
            return name(name, null);
        }
        
        public Builder name(final String str, Charset encoding) {
            this.str = str;
            if (encoding == null) {
                this.name = Util.toBytes(str);
            } else {
                this.name = Util.toBytes(str, encoding);
            }
            return this;
        }
        public Builder name(final String nameStr) {
            return name(nameStr, null);
        }
        
        public Builder alias(final String alias) throws IllegalArgumentException {
            Util.ensureNotNull(alias, this, "alias", String.class);
            this.alias.add(alias);
            return this;
        }
        
        public Name build() {
            return new Name(this);
        }
        
        private Builder() {
            this.name = null;
            this.str = null;
            this.alias = new HashSet<String>();
        }
    }
    
    public static final Builder with(final byte[] name, Charset decoding) {
        return new Builder().name(name, decoding);
    }
    public static final Builder with(final String nameStr, Charset encoding) {
        return new Builder().name(nameStr, encoding);
    }
    public static final Builder with(final byte[] name) {
        return new Builder().name(name);
    }
    public static final Builder with(final String nameStr) {
        return new Builder().name(nameStr);
    }
    
    public static final Name of(final byte[] name, Charset decoding) {
        return with(name, decoding).build();
    }
    public static final Name of(final String nameStr, Charset encoding) {
        return with(nameStr, encoding).build();
    }
    public static final Name of(final byte[] name) {
        return with(name).build();
    }
    public static final Name of(final String nameStr) {
        return with(nameStr).build();
    }
    
    private final byte[] name;
    private final String str;
    private final Set<String> alias;
    
    private Integer hc;
    private String strRep;
    
    /**
     * WARNING: This method returns the name as a MUTABLE byte-array, which means that any code
     * which has access can alter the name of this element! Do not pass the resulting array to any
     * untrusted components.
     * @return
     */
    public byte[] getName() {
        return name;
    }
    public String getStr() {
        return str;
    }
    public Set<String> getAlias() {
        return alias;
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        final Name otherName;
        if (otherObj instanceof Name) {
            otherName = (Name) otherObj;
            return Util.refEquals(this.name, otherName.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Arrays.hashCode(this.name);
        }
        return this.hc.intValue();
    }
    
    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append("<HB:");
            strGen.append(this.str);
            if (this.alias.size() > 0) {
                strGen.append("|");
                strGen.append(this.alias.size());
            }
            strGen.append(">");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    private Name(final Builder build) throws IllegalArgumentException {
        if ((build.name == null) || (build.name.length <= 0)) {
            throw new IllegalArgumentException("Null/empty name not permitted");
        }
        this.name = build.name;
        this.str = build.str;
        this.alias = Collections.unmodifiableSet(build.alias);
        this.strRep = null;
        this.hc = null;
    }
}
