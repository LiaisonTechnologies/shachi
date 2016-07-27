package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.serialization.BytesUtil;

import java.util.Arrays;

public class ParsedVersionQualifier {

    public static class Builder {
        private byte[] qualWithoutVersion;
        private Long version;

        /**
         * "Cleaned" qualifier name, with the version number suffix and delimiter (if any, i.e. in
         * the case where qualifier-based versioning was in use) are removed.
         *
         * This implementation DOES NOT make a defensive copy of the byte array, so the client is
         * responsible for ensuring that either (a) the provided array is not in use elsewhere or
         * (b) a defensive copy is made beforehand.
         * @param qualWithoutVersion
         * @return
         */
        public Builder qualWithoutVersion(final byte[] qualWithoutVersion) {
            this.qualWithoutVersion = qualWithoutVersion;
            return this;
        }

        /**
         * Version number associated with the qualifier; may be null if no versioning scheme is in
         * effect on the source schema
         * @param version
         * @return
         */
        public Builder version(final Long version) {
            this.version = version;
            return this;
        }
        public ParsedVersionQualifier build() {
            return new ParsedVersionQualifier(this);
        }
        private Builder() {}
    }

    public static Builder with() {
        return new Builder();
    }

    private final byte[] qualWithoutVersion;
    private final Long version;

    private String strRep;
    private Integer hc;

    public byte[] getQualWithoutVersion() {
        return BytesUtil.copy(this.qualWithoutVersion);
    }
    public Long getVersion() {
        return this.version;
    }
    public boolean hasVersion() {
        return (this.version != null);
    }

    @Override
    public int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = Util.hashCode(this.version);
            hCode ^= Arrays.hashCode(qualWithoutVersion);
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }

    @Override
    public boolean equals(final Object obj) {
        final ParsedVersionQualifier otherPVQ;
        if (obj == this) {
            return true;
        } else if (obj instanceof ParsedVersionQualifier){
            otherPVQ = (ParsedVersionQualifier) obj;
            return ((Util.refEquals(this.version, otherPVQ.version))
                    &&
                    (Util.refEquals(this.qualWithoutVersion, otherPVQ.qualWithoutVersion)));
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(ParsedVersionQualifier.class.getSimpleName());
            strGen.append("(len=");
            strGen.append(this.qualWithoutVersion.length);
            strGen.append(",ver=");
            strGen.append(String.valueOf(this.version));
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private ParsedVersionQualifier(final Builder build) throws IllegalArgumentException {
        Util.ensureNotNull(build.qualWithoutVersion,
                           ParsedVersionQualifier.class,
                           "qualWithoutVersion",
                           byte[].class);
        this.qualWithoutVersion = build.qualWithoutVersion;
        this.version = build.version;
        this.strRep = null;
        this.hc = null;
    }
}
