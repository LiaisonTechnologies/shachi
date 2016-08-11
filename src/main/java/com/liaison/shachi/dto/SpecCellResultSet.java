package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.exception.HBaseException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.07 10:06
 */
public class SpecCellResultSet extends CellResult<List<SingleCellResult>> {

    private static final long serialVersionUID = -611116205708897090L;

    public static class Builder {

        private HBaseException exc;
        private List<SingleCellResult> resultList;

        public Builder exc(final HBaseException exc) {
            this.exc = exc;
            return this;
        }

        public Builder result(final SingleCellResult scRes) throws IllegalArgumentException {
            Util.ensureNotNull(scRes, this, "scRes", SingleCellResult.class);
            this.resultList.add(scRes);
            return this;
        }

        public SpecCellResultSet build() throws IllegalArgumentException {
            String logMsg;

            if ((this.resultList.isEmpty()) && (this.exc != null)) {
                logMsg =
                    SpecCellResultSet.class.getSimpleName()
                        + " cannot define both data and an exception ("
                        + HBaseException.class.getName()
                        + "); exactly 1 must be defined";
                throw new IllegalArgumentException(logMsg);
            }
            if (!this.resultList.isEmpty()) {
                return new SpecCellResultSet(this.resultList);
            } else if (this.exc != null) {
                return new SpecCellResultSet(this.exc);
            } else {
                logMsg =
                    SpecCellResultSet.class.getSimpleName()
                        + " must define exactly 1 of: data list OR exception ("
                        + HBaseException.class.getName()
                        + "); existing builder has neither";
                throw new IllegalArgumentException(logMsg);
            }
        }

        private Builder() {
            this.exc = null;
            this.resultList = new LinkedList<>();
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public boolean isEmpty() {
         return ((!isSuccess()) || (getContent().isEmpty()));
    }

    private SpecCellResultSet(final List<SingleCellResult> resultList) throws IllegalArgumentException {
        super(Collections.unmodifiableList(resultList));
    }
    private SpecCellResultSet(final HBaseException exc) throws IllegalArgumentException {
        super(exc);
    }
}
