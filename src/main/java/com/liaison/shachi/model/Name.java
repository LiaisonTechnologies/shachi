/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.model;

import com.liaison.serialization.BytesUtil;
import com.liaison.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.dto.Value;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Name extends Value implements Serializable {
    
    private static final long serialVersionUID = -290504940089138756L;
    
    // ||========================================================================================||
    // ||    BUILDER (STATIC NESTED CLASS)                                                       ||
    // ||----------------------------------------------------------------------------------------||

    public static final class Builder extends AbstractValueBuilder<Name, Builder> {
        private String str;
        private Set<String> alias;
        
        @Override
        public Builder self() {
            return this;
        }

        private void setStrBasedOnInputBytes(final byte[] name, final Charset decoding) {
            if (decoding == null) {
                this.str = BytesUtil.toString(name);
            } else {
                this.str = BytesUtil.toString(name, decoding);
            }
        }
        
        public Builder name(final byte[] name, final Charset decoding, final DefensiveCopyStrategy copyStrategy) {
            value(name, copyStrategy);
            setStrBasedOnInputBytes(name, decoding);
            return this;
        }
        public Builder name(final byte[] name, final DefensiveCopyStrategy copyStrategy) {
            return name(name, ((Charset) null), copyStrategy);
        }
        
        @Deprecated
        public Builder name(final byte[] name, final Charset decoding) {
            value(name);
            setStrBasedOnInputBytes(name, decoding);
            return this;
        }
        @Deprecated
        public Builder name(final byte[] name) {
            return name(name, ((Charset) null));
        }
        
        public Builder name(final String str, final Charset encoding) {
            final byte[] strAsBytes;
            this.str = str;
            if (encoding == null) {
                strAsBytes = BytesUtil.toBytes(str);
            } else {
                strAsBytes = BytesUtil.toBytes(str, encoding);
            }
            /*
             * A defensive copy is never needed/desired here, because the bytes are derived from a
             * conversion from String, and the source string is immutable (even IF the conversion
             * itself doesn't make a copy).
             */
            value(strAsBytes, DefensiveCopyStrategy.NEVER);
            return this;
        }
        public Builder name(final String nameStr) {
            return name(nameStr, null);
        }
        
        
        @Override
        public Name build() {
            return new Name(this);
        }
        private Builder() throws IllegalArgumentException {
            super();
            this.str = null;
            this.alias = new HashSet<String>();
        }
    }
    
    // ||----(builder)---------------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    CONSTANTS                                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    private static final String ENTITY_PREFIX_FOR_TOSTRING = "!";
    
    // ||----(constants)-------------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    STATIC METHODS                                                                      ||
    // ||----------------------------------------------------------------------------------------||
    
    public static final Builder with(final byte[] name, final Charset decoding, final DefensiveCopyStrategy dcs) {
        return new Builder().name(name, decoding, dcs);
    }
    @Deprecated
    public static final Builder with(final byte[] name, final Charset decoding) {
        return new Builder().name(name, decoding);
    }
    public static final Builder with(final String nameStr, final Charset encoding) {
        return new Builder().name(nameStr, encoding);
    }
    public static final Builder with(final byte[] name, final DefensiveCopyStrategy dcs) {
        return new Builder().name(name, dcs);
    }
    @Deprecated
    public static final Builder with(final byte[] name) {
        return new Builder().name(name);
    }
    public static final Builder with(final String nameStr) {
        return new Builder().name(nameStr);
    }
    
    public static final Name of(final byte[] name, final Charset decoding, final DefensiveCopyStrategy dcs) {
        return with(name, decoding, dcs).build();
    }
    @Deprecated
    public static final Name of(final byte[] name, final Charset decoding) {
        return with(name, decoding).build();
    }
    public static final Name of(final String nameStr, final Charset encoding) {
        return with(nameStr, encoding).build();
    }
    public static final Name of(final byte[] name, final DefensiveCopyStrategy dcs) {
        return with(name, dcs).build();
    }
    @Deprecated
    public static final Name of(final byte[] name) {
        return with(name).build();
    }
    public static final Name of(final String nameStr) {
        return with(nameStr).build();
    }
    
    // ||----(static methods)--------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private final String str;
    private final Set<String> alias;
    
    private Integer hc;
    private String strRep;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: DATA ACCESS                                                       ||
    // ||----------------------------------------------------------------------------------------||
    
    public String getStr() {
        return str;
    }
    public Set<String> getAlias() {
        return alias;
    }
    
    // ||----(instance methods: data access)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    public boolean equals(final Object otherObj) {
        if (this == otherObj) {
            return true;
        }
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
        if (this.strRep == null) {
            this.strRep =
                buildStrRep(ENTITY_PREFIX_FOR_TOSTRING, (strGen) -> {
                    strGen.append(this.str);
                    if (this.alias.size() > 0) {
                        strGen.append("|");
                        strGen.append(this.alias.size());
                    }
                });
        }
        return this.strRep;
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    private Name(final Builder build) throws IllegalArgumentException {
        super(build);
        
        final byte[] valueBytes;
        
        valueBytes = getValue(DefensiveCopyStrategy.NEVER);
        if (valueBytes == null) {
            throw new IllegalArgumentException("Non-null name value must be specified");
        }
        this.str = build.str;
        this.alias = Collections.unmodifiableSet(build.alias);
        this.strRep = null;
        this.hc = null;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
