/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.exception.HBaseException;

import java.io.Serializable;

public class CellResult<X> implements Serializable {

    private static final long serialVersionUID = 6677599252592241754L;

    private final X content;
    private final HBaseException exc;

    private String strRep;
    private Integer hc;

    public boolean isSuccess() {
        return (this.exc == null);
    }
    public X getContent() {
        return this.content;
    }
    public HBaseException getExc() {
        return this.exc;
    }

    public int hashCodeSubord() {
        // no-op in default implementation
        return 0;
    }

    @Override
    public final int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = Util.hashCode(this.content);
            hCode ^= Util.hashCode(this.exc);
            hCode ^= hashCodeSubord();
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }

    public boolean equalsSubord(final CellResult otherCR) {
        // no-op in default implementation
        return true;
    }
    @Override
    public final boolean equals(final Object otherObj) {
        final CellResult otherCR;
        if (this == otherObj) {
            return true;
        } else if (otherObj instanceof CellResult) {
            otherCR = (CellResult) otherObj;
            return (Util.refEquals(this.exc, otherCR.exc)
                    && Util.refEquals(this.content, otherCR.content)
                    && equalsSubord(otherCR));
        }
        return false;
    }

    public void toStringSubord(final StringBuilder strGen) {
        // do nothing in default implementation
    }
    @Override
    public final String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(getClass().getSimpleName());
            strGen.append("(");
            toStringSubord(strGen);
            strGen.append("):{");
            if (this.content != null) {
                strGen.append(this.content);
            } else if (this.exc != null) {
                strGen.append(this.exc);
            }
            strGen.append("}");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private <E extends HBaseException> CellResult(final E exc, Class<E> excClass) throws IllegalArgumentException {
        Util.ensureNotNull(exc, this, "exc", excClass);
        this.content = null;
        this.exc = exc;
    }
    public CellResult(final HBaseException exc) throws IllegalArgumentException {
        this(exc, HBaseException.class);
    }
    public CellResult(final X content) throws IllegalArgumentException {
        Util.ensureNotNull(content, this, "content");
        this.content = content;
        this.exc = null;
    }
    public CellResult() {
        this.content = null;
        this.exc = null;
    }
}
