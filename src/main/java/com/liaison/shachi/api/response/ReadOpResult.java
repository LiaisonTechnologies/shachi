/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.response;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.api.request.frozen.ColSpecReadFrozen;
import com.liaison.shachi.api.request.impl.ColSpecRead;
import com.liaison.shachi.api.request.impl.ReadOpSpecDefault;
import com.liaison.shachi.dto.CellDatum;
import com.liaison.shachi.dto.CellResult;
import com.liaison.shachi.dto.Datum;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.dto.SingleCellResult;
import com.liaison.shachi.dto.SpecCellResultSet;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.util.SpecUtil;

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

        private static final JitLog LOG;

        /**
         * TODO: javadoc
         */
        private static final String CLOSURENAME_ADD = ReadOpResultBuilder.class.getSimpleName() + "#add";

        static {
            LOG = new JitLog(ReadOpResultBuilder.class);
        }

        /**
         * data set linking the retrieved data to the spec which caused it to be queried. data in
         * order retrieved from HBase
         * TODO: javadoc
         */
        private Map<ColSpecReadFrozen, SpecCellResultSet.Builder> dataBySpec;

        /**
         * TODO: javadoc
         */
        private Map<Object, SpecCellResultSet.Builder> dataBySpecHandle;

        /**
         * TODO: javadoc
         * @return
         */
        @Override
        public final ReadOpResultBuilder self() {
            return this;
        }

        private SingleCellResult.Builder singleCellBuilder() {
            return
                SingleCellResult
                    .getBuilder()
                    .tableRow(getTableRow());
        }
        private SingleCellResult.Builder singleCellBuilder(final FamilyQualifierPair fqp) {
            return singleCellBuilder().tableColumn(fqp);
        }
        private SingleCellResult.Builder singleCellBuilder(final ColSpecReadFrozen spec, final FamilyQualifierPair fqp) {
            return singleCellBuilder(fqp).spec(spec);
        }
        private SingleCellResult.Builder singleCellBuilder(final ColSpecReadFrozen spec) {
            return singleCellBuilder().spec(spec);
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param cellResult
         * @return
         */
        private ReadOpResultBuilder addCellResult(final ColSpecReadFrozen colSpec, SingleCellResult cellResult) {
            final Object handle;

            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecRead.class);
            Util.appendToValueInMap(this.dataBySpec,
                                    colSpec,
                                    cellResult,
                                    SpecCellResultSet.Builder::result,
                                    SpecCellResultSet::getBuilder);
            handle = colSpec.getHandle();
            if (handle != null) {
                Util.appendToValueInMap(this.dataBySpecHandle,
                                        handle,
                                        cellResult,
                                        SpecCellResultSet.Builder::result,
                                        SpecCellResultSet::getBuilder);
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
        public ReadOpResultBuilder add(final ColSpecReadFrozen colSpec, final HBaseException exc) throws IllegalArgumentException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecReadFrozen.class);
            Util.ensureNotNull(exc, CLOSURENAME_ADD, "exc", HBaseException.class);
            return addCellResult(colSpec, singleCellBuilder(colSpec).exc(exc).build());
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param exc
         * @return
         * @throws IllegalArgumentException
         * @throws IllegalStateException
         */
        public ReadOpResultBuilder add(final ColSpecReadFrozen colSpec, final FamilyQualifierPair fqp, final HBaseException exc) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecReadFrozen.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(exc, CLOSURENAME_ADD, "exc", HBaseException.class);
            return addCellResult(colSpec, singleCellBuilder(colSpec, fqp).exc(exc).build());
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param datum
         * @return
         * @throws IllegalArgumentException
         * @throws IllegalStateException
         */
        public ReadOpResultBuilder add(final ColSpecReadFrozen colSpec, final FamilyQualifierPair fqp, final Datum datum) throws IllegalArgumentException, IllegalStateException {
            Util.ensureNotNull(colSpec, CLOSURENAME_ADD, "colSpec", ColSpecReadFrozen.class);
            Util.ensureNotNull(fqp, CLOSURENAME_ADD, "fqp", FamilyQualifierPair.class);
            Util.ensureNotNull(datum, CLOSURENAME_ADD, "datum", Datum.class);
            return addCellResult(colSpec,
                                 singleCellBuilder(fqp)
                                     .datum(CellDatum
                                                .getBuilder()
                                                .datum(datum)
                                                .column(fqp)
                                                .deserializer(
                                                    SpecUtil.identifyDeserializer(
                                                        fqp,
                                                        colSpec.getColumn(),
                                                        colSpec.getFamily(),
                                                        getTableRow().getTable()))
                                                .row(getTableRow())
                                                .build())
                                     .build());
        }

        /**
         * TODO: javadoc
         * @return
         */
        public SpecCellResultSet getDataBySpec(final ColSpecReadFrozen colSpec) {
            final SpecCellResultSet.Builder cellResultSetBuilder;
            final SpecCellResultSet cellResultSet;
            final String logMethodName;

            logMethodName = LOG.enter(()->"getDataBySpec(colSpec=", ()->colSpec);
            LOG.trace(logMethodName,
                      ()->"this.dataBySpec=",
                      ()->this.dataBySpec);
            cellResultSetBuilder = this.dataBySpec.get(colSpec);
            if (cellResultSetBuilder != null) {
                cellResultSet = cellResultSetBuilder.build();
                LOG.trace(logMethodName,
                          () -> "result=",
                          () -> cellResultSet);
            } else {
                cellResultSet = null;
                LOG.trace(logMethodName,
                          () -> " (no result)");
            }
            LOG.leave(logMethodName);

            return cellResultSet;
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
            this.dataBySpec = new HashMap<>();
            this.dataBySpecHandle = new HashMap<>();
        }
    }
    
    private static final String OPRESULT_TYPE_STR = "READ";

    private static <E> E getFirst(final List<E> list) {
        E result = null;
        if ((list != null) && (!list.isEmpty())) {
            result = list.get(0);
        }
        return result;
    }

    private static <K> Map<K, SpecCellResultSet> buildResultMapFromMutableResultMap(final Map<K, SpecCellResultSet.Builder> source) {
        final Map<K, SpecCellResultSet> dataMapTemp;
        /*
         * MUST use LinkedHashMap here in order to preserve order received from HBase
         */
        dataMapTemp = new LinkedHashMap<>();
        source
            .entrySet()
            .stream()
            .forEachOrdered((entry)->dataMapTemp.put(entry.getKey(), entry.getValue().build()));
        return Collections.unmodifiableMap(dataMapTemp);
    }

    /**
     * TODO: javadoc
     * @return
     */
    public static ReadOpResultBuilder getBuilder() {
        return new ReadOpResultBuilder();
    }

    /**
     * data set linking the retrieved data to the spec which caused it to be queried. data in
     * order retrieved from HBase
     * TODO: javadoc
     */
    private final Map<ColSpecReadFrozen, SpecCellResultSet> dataBySpec;
    /**
     * TODO: javadoc
     */
    private final Map<Object, SpecCellResultSet> dataBySpecHandle;

    private <X> X toContent(final CellResult<X> cellRes) throws HBaseException {
        final HBaseException hbExc;
        X result = null;

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
    public Map<ColSpecReadFrozen, SpecCellResultSet> getDataBySpec() {
        return Collections.unmodifiableMap(this.dataBySpec);
    }

    /**
     * TODO: javadoc
     * @param key
     * @param dataMap
     * @param <K>
     * @return
     * @throws HBaseException
     */
    private <K> List<CellDatum> getDataBy(final K key, final Map<K, SpecCellResultSet> dataMap) throws HBaseException {
        final List<CellDatum> dataList;
        final List<SingleCellResult> cellResList;
        CellDatum content;

        dataList = new LinkedList<>();
        cellResList = toContent(dataMap.get(key));
        if (cellResList != null) {
            for (SingleCellResult cellRes : cellResList) {
                content = toContent(cellRes);
                if (content != null) {
                    dataList.add(content);
                }
            }
        }
        return Collections.unmodifiableList(dataList);
    }

    /**
     * TODO: javadoc
     * @param key
     * @param dataMap
     * @param <K>
     * @return
     * @throws HBaseException
     */
    private <K> Map<FamilyQualifierPair, CellDatum> getDataMapBy(final K key, final Map<K, SpecCellResultSet> dataMap) throws HBaseException {
        final Map<FamilyQualifierPair, CellDatum> dataResultMap;
        final List<SingleCellResult> cellResList;
        CellDatum content;

        dataResultMap = new HashMap<>();
        cellResList = toContent(dataMap.get(key));
        if (cellResList != null) {
            for (SingleCellResult cellRes : cellResList) {
                content = toContent(cellRes);
                if (content != null) {
                    dataResultMap.put(content.getTableColumn(), content);
                }
            }
        }
        return Collections.unmodifiableMap(dataResultMap);
    }

    /**
     * TODO: javadoc
     * @param colSpec
     * @return
     * @throws HBaseException
     */
    public Map<FamilyQualifierPair, CellDatum> getDataMap(final ColSpecReadFrozen colSpec) throws HBaseException {
        return getDataMapBy(colSpec, this.dataBySpec);
    }
    /**
     * TODO: javadoc
     * @param colSpec
     * @return
     * @throws HBaseException
     */
    public List<CellDatum> getData(final ColSpecReadFrozen colSpec) throws HBaseException {
        return getDataBy(colSpec, this.dataBySpec);
    }
    public CellDatum getSingleData(final ColSpecReadFrozen colSpec) throws HBaseException {
        return getFirst(getData(colSpec));
    }

    /**
     * TODO: javadoc
     * @param handle
     * @return
     * @throws Exception
     */
    public Map<FamilyQualifierPair, CellDatum> getDataMap(final Object handle) throws HBaseException {
        return getDataMapBy(handle, this.dataBySpecHandle);
    }
    /**
     * TODO: javadoc
     * @param handle
     * @return
     * @throws Exception
     */
    public List<CellDatum> getData(final Object handle) throws HBaseException {
        return getDataBy(handle, this.dataBySpecHandle);
    }
    public CellDatum getSingleData(final Object handle) throws HBaseException {
        return getFirst(getData(handle));
    }

    /**
     * TODO: javadoc
     * @param otherOpResult
     * @return
     */
    @Override
    protected boolean deepEquals(OpResult<?> otherOpResult) {
        final ReadOpResult otherReadOpResult;
        if (otherOpResult instanceof ReadOpResult) {
            otherReadOpResult = (ReadOpResult) otherOpResult;
            return Util.refEquals(this.dataBySpec, otherReadOpResult.dataBySpec);
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
        strGen.append(this.dataBySpec);
        strGen.append("}");
    }
    
    private ReadOpResult(final ReadOpResultBuilder build) {
        super(build);

        final Map<ColSpecReadFrozen, SpecCellResultSet> dataBySpecTemp;
        final Map<Object, SpecCellResultSet> dataBySpecHandleTemp;
        String logMsg;
        
        if ((this.getException() != null) && (build.dataBySpec.size() > 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " may not reference both a row-level exception and a non-empty data result set";
            throw new IllegalStateException(logMsg);
        }
        /*
        TODO? decide whether this is a necessary restriction; right now, it's causing problems
        if ((this.getException() == null) && (build.data.size() <= 0)) {
            logMsg = 
                OpResult.class.getSimpleName()
                + " must reference exactly one of: a row-level exception or a data result set";
            throw new IllegalStateException(logMsg);
        }
        */

        this.dataBySpec = buildResultMapFromMutableResultMap(build.dataBySpec);
        this.dataBySpecHandle = buildResultMapFromMutableResultMap(build.dataBySpecHandle);
    }
}
