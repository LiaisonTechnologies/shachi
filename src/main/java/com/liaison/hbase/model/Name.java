package com.liaison.hbase.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.util.Util;

public class Name extends Value implements Serializable {
    
    private static final long serialVersionUID = -290504940089138756L;

    public static final class Builder extends AbstractValueBuilder<Name, Builder> {
        private String str;
        private Set<String> alias;
        
        @Override
        public Builder self() {
            return this;
        }

        public Builder name(final byte[] name, Charset decoding) {
            value(name);
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
            final byte[] strAsBytes;
            this.str = str;
            if (encoding == null) {
                strAsBytes = Util.toBytes(str);
            } else {
                strAsBytes = Util.toBytes(str, encoding);
            }
            value(strAsBytes);
            return this;
        }
        public Builder name(final String nameStr) {
            return name(nameStr, null);
        }
        
        @Override
        public Name build() {
            return new Name(this);
        }
        private Builder(final HBaseContext context) throws IllegalArgumentException {
            super(context);
            this.str = null;
            this.alias = new HashSet<String>();
        }
    }
    
    public static final Builder with(final byte[] name, final Charset decoding, final HBaseContext context) {
        return new Builder(context).name(name, decoding);
    }
    public static final Builder with(final String nameStr, final Charset encoding, final HBaseContext context) {
        return new Builder(context).name(nameStr, encoding);
    }
    public static final Builder with(final byte[] name, final HBaseContext context) {
        return new Builder(context).name(name);
    }
    public static final Builder with(final String nameStr, final HBaseContext context) {
        return new Builder(context).name(nameStr);
    }
    
    public static final Name of(final byte[] name, final Charset decoding, final HBaseContext context) {
        return with(name, decoding, context).build();
    }
    public static final Name of(final String nameStr, final Charset encoding, final HBaseContext context) {
        return with(nameStr, encoding, context).build();
    }
    public static final Name of(final byte[] name, final HBaseContext context) {
        return with(name, context).build();
    }
    public static final Name of(final String nameStr, final HBaseContext context) {
        return with(nameStr, context).build();
    }
    
    private final String str;
    private final Set<String> alias;
    
    private Integer hc;
    private String strRep;
    
    public String getStr() {
        return str;
    }
    public Set<String> getAlias() {
        return alias;
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj instanceof Name) {
            return super.equals(otherObj);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(super.hashCode());
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
        super(build);
        if ((getValue() == null) || (getValue().length <= 0)) {
            throw new IllegalArgumentException("Null/empty name not permitted");
        }
        this.str = build.str;
        this.alias = Collections.unmodifiableSet(build.alias);
        this.strRep = null;
        this.hc = null;
    }
}
