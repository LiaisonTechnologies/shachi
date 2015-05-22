package com.liaison.hbase.resmgr;

public final class ResourceConnectTolerance {

    public static final class Builder {
        
        private Integer attemptsMax;
        private Long retryDelayInit;
        private Long retryDelayMax;
        private Integer retryDelayMultiplier;

        public Builder attemptsMax(final int attemptsMax) {
            this.attemptsMax = Integer.valueOf(attemptsMax);
            return this;
        }
        public Builder retryDelayInit(final long retryDelayInit) {
            this.retryDelayInit = Long.valueOf(retryDelayInit);
            return this;
        }
        public Builder retryDelayMax(final long retryDelayMax) {
            this.retryDelayMax = Long.valueOf(retryDelayMax);
            return this;
        }
        public Builder retryDelayMultiplier(final int retryDelayMultiplier) {
            this.retryDelayMultiplier = Integer.valueOf(retryDelayMultiplier);
            return this;
        }

        private Builder() {
            this.attemptsMax = null;
            this.retryDelayInit = null;
            this.retryDelayMax = null;
            this.retryDelayMultiplier = null;
        }
    }
    
    private static final int DEFAULT_ATTEMPTS_MAX = 10;
    private static final long DEFAULT_RETRYDELAY_INIT_MS = 10;
    private static final long DEFAULT_RETRYDELAY_MAX_MS = 5000;
    private static final int DEFAULT_RETRYDELAY_MULTIPLIER = 2;
    
    public static final ResourceConnectTolerance DEFAULT = new ResourceConnectTolerance();

    private final int attemptsMax;
    private final long retryDelayInit;
    private final long retryDelayMax;
    private final int retryDelayMultiplier;
    
    private Integer hc;
    private String strRep;
    
    public int getAttemptsMax() {
        return attemptsMax;
    }
    public long getRetryDelayInit() {
        return retryDelayInit;
    }
    public long getRetryDelayMax() {
        return retryDelayMax;
    }
    public int getRetryDelayMultiplier() {
        return retryDelayMultiplier;
    }
    
    @Override
    public int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = this.attemptsMax;
            hCode ^= this.retryDelayInit;
            hCode ^= this.retryDelayMax;
            hCode ^= this.retryDelayMultiplier;
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        final ResourceConnectTolerance otherRCT;
        if (otherObj instanceof ResourceConnectTolerance) {
            otherRCT = (ResourceConnectTolerance) otherObj;
            return ((this.attemptsMax == otherRCT.attemptsMax)
                    && (this.retryDelayInit == otherRCT.retryDelayInit)
                    && (this.retryDelayMax == otherRCT.retryDelayMax)
                    && (this.retryDelayMultiplier == otherRCT.retryDelayMultiplier));
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(getClass().getSimpleName());
            strGen.append("(attemptsMax=");
            strGen.append(this.attemptsMax);
            strGen.append(",retryDelayInit=");
            strGen.append(this.retryDelayInit);
            strGen.append(",retryDelayMax=");
            strGen.append(this.retryDelayMax);
            strGen.append(",retryDelayMultiplier=");
            strGen.append(this.retryDelayMultiplier);
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    private ResourceConnectTolerance(final Builder build) throws IllegalArgumentException {
        if ((build != null) && (build.attemptsMax != null)) {
            this.attemptsMax = build.attemptsMax.intValue();
        } else {
            this.attemptsMax = DEFAULT_ATTEMPTS_MAX;
        }
        if ((build != null) && (build.retryDelayInit != null)) {
            this.retryDelayInit = build.retryDelayInit.longValue();
        } else {
            this.retryDelayInit = DEFAULT_RETRYDELAY_INIT_MS;
        }
        if ((build != null) && (build.retryDelayMax != null)) {
            this.retryDelayMax = build.retryDelayMax.longValue();
        } else {
            this.retryDelayMax = DEFAULT_RETRYDELAY_MAX_MS;
        }
        if ((build != null) && (build.retryDelayMultiplier != null)) {
            this.retryDelayMultiplier = build.retryDelayMultiplier.intValue();
        } else {
            this.retryDelayMultiplier = DEFAULT_RETRYDELAY_MULTIPLIER;
        }
    }
    private ResourceConnectTolerance() {
        this(null);
    }
}
