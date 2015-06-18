/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.response;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.frozen.ColSpecReadFrozen;
import com.liaison.hbase.api.request.impl.ColSpecRead;
import com.liaison.hbase.api.request.impl.ReadOpSpecDefault;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.dto.SingleCellResult;
import com.liaison.hbase.exception.HBaseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: javadoc
 */
public class ReadOpResult extends OpResult<ReadOpSpecDefault> {

    private static final long serialVersionUID = 8027722441667395989L;

    /**
     * TODO: javadoc
     */
    public static class ReadOpResultBuilder extends OpResultBuilder<ReadOpSpecDefault, ReadOpResult, ReadOpResultBuilder> {
        /**
         * TODO: javadoc
         */
        private static final String CLOSURENAME_ADD = ReadOpResultBuilder.class.getSimpleName() + "#add";

        /**
         * literal data set; stores references to the literal cells retrieved from HBase
         * TODO: javadoc
         */
        private Map<FamilyQualifierPair, SingleCellResult> data;
        /*
         * TODO: The data map really needs to be Map<FamilyQualifierPair, List<SingleCellResult>
         * in order to accommodate cases where a range of timestamps is retrieved (all of which
         * bear the same family and qualifier)
         */

        /**
         * data set linking the retrieved data to the spec which caused it to be queried. data in
         * order retrieved from HBase
         * TODO: javadoc
         */
        private Map<ColSpecReadFrozen, List<SingleCellResult>> dataBySpec;

        /**
         * TODO: javadoc
         * @return
         */
        @Override
        public final ReadOpResultBuilder self() {
            return this;
        }

        /**
         * TODO: javadoc
         * @param fqp
         * @param cellResult
         * @return
         */
        private ReadOpResultBuilder addCellResult(final FamilyQualifierPair fqp, final SingleCellResult cellResult) {
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            this.data.put(fqp, cellResult);
            return self();
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param cellResult
         * @return
         */
        private ReadOpResultBuilder addCellResult(final ColSpecReadFrozen colSpec, SingleCellResult cellResult) {
            List<SingleCellResult> resList;
            final List<SingleCellResult> existingResList;

            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);

            resList = this.dataBySpec.get(colSpec);
            if (resList == null) {
                resList = new LinkedList<SingleCellResult>();
                existingResList = this.dataBySpec.putIfAbsent(colSpec, resList);
                if (existingResList != null) {
                    resList = existingResList;
                }
                resList.add(cellResult);
            }
            return self();
        }

        /**
         * TODO: javadoc
         * @param fqp
         * @param exc
         * @return
         * @throws IllegalArgumentException
         */
        public ReadOpResultBuilder add(final FamilyQualifierPair fqp, final HBaseException exc) throws IllegalArgumentException {
            Util.ensureNotNull(exc, CLOSURENAME_ADD, "exc", HBaseException.class);
            return addCellResult(fqp, new SingleCellResult(exc));
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param exc
         * @return
         * @throws IllegalArgumentException
         * @throws IllegalStateException
         */
        public ReadOpResultBuilder add(final ColSpecReadFrozen colSpec, final HBaseException exc) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(exc, CLOSURENAME_ADD, "exc", HBaseException.class);
            return addCellResult(colSpec, new SingleCellResult(exc));
        }

        /**
         * TODO: javadoc
         * @param fqp
         * @param datum
         * @return
         * @throws IllegalArgumentException
         */
        public ReadOpResultBuilder add(final FamilyQualifierPair fqp, final Datum datum) throws IllegalArgumentException {
            Util.ensureNotNull(datum, CLOSURENAME_ADD, "datum", Datum.class);
            return addCellResult(fqp, new SingleCellResult(datum));
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param datum
         * @return
         * @throws IllegalArgumentException
         * @throws IllegalStateException
         */
        public ReadOpResultBuilder add(final ColSpecReadFrozen colSpec, final Datum datum) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(datum, CLOSURENAME_ADD, "datum", Datum.class);
            return addCellResult(colSpec, new SingleCellResult(datum));
        }

        /**
         * TODO: javadoc
         * @return
         */
        public SingleCellResult getData(final FamilyQualifierPair fqp) {
            return this.data.get(fqp);
        }

        /**
         * TODO: javadoc
         * @return
         */
        public List<SingleCellResult> getDataBySpec(final ColSpecReadFrozen colSpec) {
            return Collections.unmodifiableList(this.dataBySpec.get(colSpec));
        }

        /**
         * TODO: javadoc
         * @return
         */
        @Override
        public final ReadOpResult build() {
            return new ReadOpResult(this);
        }

        /**
         * TODO: javadoc
         */
        private ReadOpResultBuilder() {
            super();
            /*
             * IMPORTANT: This *must* be a LinkedHashMap (rather than a simple HashMap) in order to
             * preserve the order of data elements as retrieved by HBase.
             */
            this.data = new LinkedHashMap<>();
            this.dataBySpec = new HashMap<>();
        }
    }
    
    private static final String OPRESULT_TYPE_STR = "READ";

    /**
     * TODO: javadoc
     * @return
     */
    public static ReadOpResultBuilder getBuilder() {
        return new ReadOpResultBuilder();
    }

    /**
     * literal data set; stores references to the literal cells retrieved from HBase
     * TODO: javadoc
     */
    private final Map<FamilyQualifierPair, SingleCellResult> data;

    /**
     * data set linking the retrieved data to the spec which caused it to be queried. data in
     * order retrieved from HBase
     * TODO: javadoc
     */
    private final Map<ColSpecReadFrozen, List<SingleCellResult>> dataBySpec;

    private Datum toContent(final SingleCellResult cellRes) throws HBaseException {
        final HBaseException hbExc;
        Datum result = null;

        if (cellRes != null) {
            hbExc = cellRes.getExc();
            if (hbExc != null) {
                throw hbExc;
            }
            result = cellRes.getContent();
        }
        return result;
    }

    /**
     * TODO: javadoc
     * @return
     */
    public Map<FamilyQualifierPair, SingleCellResult> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    /**
     * TODO: javadoc
     * @param fqp
     * @return
     * @throws HBaseException
     */
    public Datum getData(final FamilyQualifierPair fqp) throws HBaseException {
        return toContent(this.data.get(fqp));
    }

    /**
     * TODO: javadoc
     * @return
     */
    public Map<ColSpecReadFrozen, List<SingleCellResult>> getDataBySpec() {
        /*
         * TODO
         * Collections.unmodifiableMap doesn't do anything about the internal lists, which are
         * still mutable -- is there any way to address that?
         */
        return Collections.unmodifiableMap(this.dataBySpec);
    }

    /**
     * TODO: javadoc
     * @param colSpec
     * @return
     * @throws HBaseException
     */
    public List<Datum> getData(final ColSpecReadFrozen colSpec) throws HBaseException {
        final List<Datum> dataList;
        final List<SingleCellResult> cellResList;
        Datum content;

        dataList = new LinkedList<Datum>();
        cellResList = this.dataBySpec.get(colSpec);
        if (cellResList == null) {
            return Collections.unmodifiableList(dataList);
        }
        for (SingleCellResult cellRes : cellResList) {
            content = toContent(cellRes);
            if (content != null) {
                dataList.add(content);
            }
        }
        return Collections.unmodifiableList(dataList);
    }

    /**
     *
     * @param otherOpResult
     * @return
     */
    @Override
    protected boolean deepEquals(OpResult<?> otherOpResult) {
        final ReadOpResult otherReadOpResult;
        if (otherOpResult instanceof ReadOpResult) {
            otherReadOpResult = (ReadOpResult) otherOpResult;
            return Util.refEquals(this.data, otherReadOpResult.data);
        }
        return false;
    }
    
    @Override
    protected String getOpResultTypeStr() {
        return OPRESULT_TYPE_STR;
    }
    
    @Override
    protected void prepareStrRepAdditional(StringBuilder strGen) {
        strGen.append("{data=");
        strGen.append(this.data);
        strGen.append("}");
    }
    
    private ReadOpResult(final ReadOpResultBuilder build) {
        super(build);
        
        String logMsg;
        
        if ((this.getException() != null) && (build.data.size() > 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " may not reference both a row-level exception and a non-empty data result set";
            throw new IllegalStateException(logMsg);
        }
        if ((this.getException() == null) && (build.data.size() <= 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " must reference exactly one of: a row-level exception or a data result set";
            throw new IllegalStateException(logMsg);
        }
        this.data = Collections.unmodifiableMap(build.data);
        this.dataBySpec = Collections.unmodifiableMap(build.dataBySpec);
    }
}
