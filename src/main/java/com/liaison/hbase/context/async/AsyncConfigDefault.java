/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.context.async;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class AsyncConfigDefault implements AsyncConfig {
    
    public static final class Builder {
        private boolean asyncEnabled;
        private Integer asyncPoolMinSize;
        private Integer asyncPoolMaxSize;
        public Builder enabled() {
            this.asyncEnabled = true;
            return this;
        }
        public Builder disabled() {
            this.asyncEnabled = false;
            return this;
        }
        public Builder asyncPoolMinSize(final int asyncPoolMinSize) {
            this.asyncPoolMinSize = Integer.valueOf(asyncPoolMinSize);
            return this;
        }
        public Builder asyncPoolMaxSize(final int asyncPoolMaxSize) {
            this.asyncPoolMaxSize = Integer.valueOf(asyncPoolMaxSize);
            return this;
        }
        public AsyncConfigDefault build() {
            return new AsyncConfigDefault(this);
        }
        private Builder() {
            this.asyncEnabled = false;
            this.asyncPoolMinSize = null;
            this.asyncPoolMaxSize = null;
        }
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    private final boolean asyncEnabled;
    private final Integer asyncPoolMinSize;
    private final Integer asyncPoolMaxSize;
    
    private Integer hc;
    private String strRep;
    
    @Override
    public boolean isAsyncEnabled() {
        return this.asyncEnabled;
    }
    @Override
    public Integer getAsyncPoolMinSize() {
        return this.asyncPoolMinSize;
    }
    @Override
    public Integer getAsyncPoolMaxSize() {
        return this.asyncPoolMaxSize;
    }
    @Override
    public int getMinSizeForThreadPool() {
        return (this.asyncPoolMinSize == null)?0:this.asyncPoolMinSize.intValue();
    }
    @Override
    public int getMaxSizeForThreadPool() {
        return (this.asyncPoolMaxSize == null)?Integer.MAX_VALUE:this.asyncPoolMaxSize.intValue();
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        AsyncConfig otherAsyncConf;
        if (otherObj instanceof AsyncConfig) {
            otherAsyncConf = (AsyncConfig) otherObj;
            return ((this.asyncEnabled == otherAsyncConf.isAsyncEnabled())
                    && (getMinSizeForThreadPool() == otherAsyncConf.getMinSizeForThreadPool())
                    && (getMaxSizeForThreadPool() == otherAsyncConf.getMaxSizeForThreadPool()));
        }
        return false;
    }
    @Override
    public int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = Boolean.hashCode(this.asyncEnabled);
            hCode ^= getMinSizeForThreadPool();
            hCode ^= getMaxSizeForThreadPool();
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }
    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(AsyncConfig.class.getSimpleName());
            strGen.append(":");
            if (this.asyncEnabled) {
                strGen.append("ENABLED:(pool-min=");
                strGen.append(getMinSizeForThreadPool());
                strGen.append(",pool-max=");
                if (getMaxSizeForThreadPool() == Integer.MAX_VALUE) {
                    strGen.append("INFINITE");
                } else {
                    strGen.append(this.asyncPoolMaxSize);
                }
                strGen.append(")");
            } else {
                strGen.append("DISABLED");
            }
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private AsyncConfigDefault(final Builder build) throws IllegalArgumentException {
        String logMsg;
        this.asyncEnabled = build.asyncEnabled;
        if (build.asyncEnabled) {
            // Assume that any negative value for minimum size is equivalent to zero/null
            if ((build.asyncPoolMinSize == null) || (build.asyncPoolMinSize.intValue() < 0)) {
                this.asyncPoolMinSize = null;
            } else {
                this.asyncPoolMinSize = build.asyncPoolMinSize;
            }
            // Assume that any negative value for maximum size is equivalent to infinity (null)
            if ((build.asyncPoolMaxSize == null) || (build.asyncPoolMaxSize.intValue() < 0)) {
                this.asyncPoolMaxSize = null;
            } else if (build.asyncPoolMaxSize.intValue() == 0) {
                logMsg = "Maximum pool size must be at least 1 for async-enabled; was: "
                         + build.asyncPoolMaxSize;
                throw new IllegalArgumentException(logMsg);
            } else {
                this.asyncPoolMaxSize = build.asyncPoolMaxSize;
            }
            if ((this.asyncPoolMinSize != null)
                && (this.asyncPoolMaxSize != null)
                && (this.asyncPoolMinSize.intValue() > this.asyncPoolMaxSize.intValue())) {
                logMsg = "Maximum pool size must be greater-than-or-equal-to minimum pool size for"
                         + " async-enabled; current: min="
                         + this.asyncPoolMinSize
                         + ",max="
                         + this.asyncPoolMaxSize;
                throw new IllegalArgumentException(logMsg);
            }
        } else {
            this.asyncPoolMaxSize = null;
            this.asyncPoolMinSize = null;
        }
    }
}
