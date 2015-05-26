/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.dto;

import java.io.Serializable;

import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.util.Util;

public class CellResult implements Serializable {

    private static final long serialVersionUID = 6170892213144914385L;
    
    private final Datum datum;
    private final HBaseException exc;
    
    private String strRep;
    private Integer hc;
    
    public boolean isSuccess() {
        return (this.exc == null);
    }
    public Datum getDatum() {
        return this.datum;
    }
    public HBaseException getExc() {
        return this.exc;
    }
    
    @Override
    public int hashCode() {
        if (this.hc == null) {
            this.hc = Integer.valueOf(Util.hashCode(this.datum) ^ Util.hashCode(this.exc));
        }
        return this.hc.intValue();
    }
    
    @Override
    public boolean equals(final Object otherObj) {
        final CellResult otherCR;
        if (otherObj instanceof CellResult) {
            otherCR = (CellResult) otherObj;
            return (Util.refEquals(this.exc, otherCR.exc)
                    && Util.refEquals(this.datum, otherCR.datum));
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append("{=");
            if (this.datum != null) {
                strGen.append(this.datum);
            } else if (this.exc != null) {
                strGen.append(this.exc);
            } else {
                strGen.append("--");
            }
            strGen.append("}");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }
    
    private <E extends HBaseException> CellResult(final E exc, Class<E> excClass) throws IllegalArgumentException {
        Util.ensureNotNull(exc, this, "exc", Datum.class);
        this.datum = null;
        this.exc = exc;
    }
    public CellResult(final HBaseException exc) throws IllegalArgumentException {
        this(exc, HBaseException.class);
    }
    public CellResult(final Datum datum) throws IllegalArgumentException {
        Util.ensureNotNull(datum, this, "datum", Datum.class);
        this.datum = datum;
        this.exc = null;
    }
    public CellResult() {
        this.datum = null;
        this.exc = null;
    }
}
