/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.response;

import com.liaison.commons.BytesUtil;
import com.liaison.commons.DefensiveCopyStrategy;
import com.liaison.commons.log.LogMeMaybe;
import com.liaison.hbase.api.request.frozen.ColSpecReadFrozen;
import com.liaison.hbase.api.request.frozen.ReadOpSpecFrozen;
import com.liaison.hbase.api.request.frozen.WriteOpSpecFrozen;
import com.liaison.hbase.api.request.impl.ColSpecRead;
import com.liaison.hbase.api.request.impl.OperationSpec;
import com.liaison.hbase.api.request.impl.ReadOpSpecDefault;
import com.liaison.hbase.api.request.impl.RowSpec;
import com.liaison.hbase.api.request.impl.TableRowOpSpec;
import com.liaison.hbase.api.request.impl.WriteOpSpecDefault;
import com.liaison.hbase.api.response.ReadOpResult.ReadOpResultBuilder;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.dto.SpecCellResultSet;
import com.liaison.hbase.exception.HBaseNoCellException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpResultSet implements Serializable {
    
    private static final long serialVersionUID = 7900478648783128880L;

    private static final LogMeMaybe LOG;

    static {
        LOG = new LogMeMaybe(OpResultSet.class);
    }
    
    private final Map<OperationSpec<?>, OpResult<?>> dataBySpec;
    private final Map<Object, OpResult<?>> dataByHandle;
    
    private FamilyQualifierPair generateFQP(final Cell resCell) {
        final byte[] cellFamily;
        final byte[] cellQual;
        final FamilyModel family;
        final QualModel qual;

        /*
         * The HBase Javadoc API for some reason does not include a description for
         * CellUtil#cloneFamily or for CellUtil#cloneQualifier, but the implementation shows that
         * the value is *copied* from the original, so there is no need to make an additional
         * defensive copy, regardless of the value of this.copyStrategy.
         */
        cellFamily = CellUtil.cloneFamily(resCell);
        cellQual = CellUtil.cloneQualifier(resCell);
        /*
         * TODO
         * Using DefensiveCopyStrategy.NEVER here rather than the provided copy strategy
         * (this.copyStrategy), because assuming that the clone operations in
         * CellUtil#cloneFamily and CellUtil#cloneQualifier are sufficient. Leaving a to-do marker
         * here for the time being just in case; may want to return and validate that assumption
         * before declaring this code base Production-ready.
         */
        family = FamilyModel.of(Name.of(cellFamily, DefensiveCopyStrategy.NEVER));
        qual = QualModel.of(Name.of(cellQual, DefensiveCopyStrategy.NEVER));
        return FamilyQualifierPair.of(family, qual);
    }

    private void addToResultBuilderIndexedToColumn(final ReadOpResultBuilder readResBuild, final ColSpecReadFrozen colSpec, final Datum datum, final FamilyQualifierPair fqp, final int logCellIndex, final int logCellTotalCount, final Object logAssoc, final String logMethodName) {
        readResBuild.add(colSpec, fqp, datum);
        LOG.trace(logMethodName,
                 ()->"cell ",
                 ()->Integer.valueOf(logCellIndex),
                 ()->"/",
                 ()->Integer.valueOf(logCellTotalCount),
                 ()->" added to data result set with association ",
                 ()->logAssoc,
                 ()->" for column: ",
                 ()->colSpec);
    }

    private void populateContentForCell(final ReadOpResultBuilder readResBuild, final ReadOpSpecDefault readSpec, final Cell resCell, final int cellIndex, final int cellTotalCount, final String logMethodName) {
        Datum datum = null;
        byte[] content;
        final int contentSize;
        Long contentTS = null;
        final FamilyQualifierPair fqp;
        final Datum datumForLog;

        LOG.trace(logMethodName,
                  ()->"processing cell ",
                  ()->Integer.valueOf(cellIndex),
                  ()->"/",
                  ()->Integer.valueOf(cellTotalCount),
                  ()->"...");
        if (resCell != null) {
            fqp = generateFQP(resCell);
            /*
             * The HBase Javadoc API for some reason does not include a description for
             * CellUtil#cloneValue, but the implementation shows that the value is *copied*
             * from the original, so there is no need to make an additional defensive copy,
             * regardless of the value of this.copyStrategy.
             */
            content = CellUtil.cloneValue(resCell);
            contentTS = Long.valueOf(resCell.getTimestamp());
            /*
             * TODO
             * At the moment, the code assumes that an empty return value (byte array of
             * zero length) and a null return value are equivalent; need to determine if
             * that is a valid assumption, and change the logic, if not.
             */
            content = BytesUtil.simplify(content);
            /*
             * TODO
             * As a follow-on to the previous to-do comment, the code as-is ignores any empty
             * values, and does not pass them back to the client; should this be changed?
             */
            if (content != null) {
                contentSize = content.length;
                LOG.trace(logMethodName,
                    ()->"cell ",
                    ()->Integer.valueOf(cellIndex),
                    ()->"/",
                    ()->Integer.valueOf(cellTotalCount),
                    ()->" size (bytes): ",
                    ()->Integer.valueOf(contentSize));
                /*
                 * TODO
                 * Using DefensiveCopyStrategy.NEVER here rather than the provided copy
                 * strategy (this.copyStrategy), because assuming that the clone operation in
                 * CellUtil#cloneValue is sufficient (particularly since once this loop cycle
                 * terminates, the reference to content in the array will be the *only*
                 * reference pointing to it). Leaving a to-do marker here for the time being
                 * just in case; may want to return and validate that assumption before
                 * declaring this code base Production-ready.
                 */
                datum =
                    Datum.of(content,
                        contentTS.longValue(),
                        DefensiveCopyStrategy.NEVER);
                /*
                 * Add this data cell to the master result list corresponding to the read spec,
                 * i.e. the result list not segregated by source column specification
                 */
                readResBuild.add(fqp, datum);
                LOG.trace(logMethodName,
                          ()->"cell ",
                          ()->Integer.valueOf(cellIndex),
                          ()->"/",
                          ()->Integer.valueOf(cellTotalCount),
                          ()->" added to master data result set");
                /*
                 * For any column specifications which are associated with this data cell based
                 * upon the *combination* of family and qualifier (i.e. column specs which
                 * specified both), add this data cell to the result list for the column spec.
                 */
                for (ColSpecReadFrozen colSpec : readSpec.getColumnAssoc(fqp)) {
                    addToResultBuilderIndexedToColumn(readResBuild,
                                                      colSpec,
                                                      datum,
                                                      fqp,
                                                      cellIndex,
                                                      cellTotalCount,
                                                      fqp,
                                                      logMethodName);
                }
                /*
                 * For any column specifications which are associated with this data cell based
                 * upon the *combination* of family and qualifier (i.e. column specs which
                 * specified both), add this data cell to the result list for the column spec.
                 */
                for (ColSpecReadFrozen colSpec : readSpec.getColumnAssoc(fqp.getFamily())) {
                    addToResultBuilderIndexedToColumn(readResBuild,
                                                      colSpec,
                                                      datum,
                                                      fqp,
                                                      cellIndex,
                                                      cellTotalCount,
                                                      fqp.getFamily(),
                                                      logMethodName);
                }
            }
        }
        datumForLog = datum;
        LOG.trace(logMethodName,
                  ()->"cell ",
                  ()->Integer.valueOf(cellIndex),
                  ()->"/",
                  ()->Integer.valueOf(cellTotalCount),
                  ()->" processed: ",
                  ()->datumForLog);
    }

    private void populateContent(final ReadOpResultBuilder readResBuild, final ReadOpSpecDefault readSpec, final Result res) {
        final String logMethodName;
        final Cell[] resCells;
        final int resCellCount;
        int iterCount;
        Datum datum = null;
        byte[] content = null;
        Long contentTS = null;
        FamilyQualifierPair fqp = null;

        logMethodName =
            LOG.enter(()->"populateContent(spec:",
                      ()->readSpec,
                      ()->")");

        resCells = res.rawCells();
        resCellCount = resCells.length;
        LOG.trace(logMethodName, ()->"result-cells.count=", ()->Integer.valueOf(resCellCount));

        iterCount = 0;
        for (Cell resCell : res.rawCells()) {
            iterCount++;
            populateContentForCell(readResBuild,
                                   readSpec,
                                   resCell,
                                   iterCount,
                                   resCellCount,
                                   logMethodName);
        }
    }
    
    private <O extends TableRowOpSpec<O>> void storeResult(final O origin, final OpResult<O> opRes) {
        this.dataBySpec.put(origin, opRes);
        this.dataByHandle.put(origin.getHandle(), opRes);
    }
    
    /**
     * TODO
     * @param readSpec
     * @param res
     * @throws HBaseTableRowException
     */
    public void assimilate(final ReadOpSpecDefault readSpec, final Result res) throws HBaseTableRowException {
        String logMsg;
        final ReadOpResultBuilder opResBuild;
        final RowSpec<ReadOpSpecDefault> rowSpec;
        SpecCellResultSet readColSpecResult;

        rowSpec = readSpec.getTableRow();
        try {
            opResBuild = ReadOpResult.getBuilder().origin(readSpec);
            populateContent(opResBuild, readSpec, res);


            for (ColSpecRead<ReadOpSpecDefault> readColSpec : readSpec.getWithColumn()) {
                readColSpecResult = opResBuild.getDataBySpec(readColSpec);
                if ((!readColSpec.isOptional())
                    && ((readColSpecResult == null) || (readColSpecResult.isEmpty()))) {
                    logMsg = "READ (handle:'"
                             + readSpec.getHandle()
                             + "') returned no Cell for table/row "
                             + rowSpec
                             + " and required column "
                             + readColSpec;
                    opResBuild.add(readColSpec,
                                   new HBaseNoCellException(rowSpec, readColSpec, logMsg));
                }
            }
            storeResult(readSpec, opResBuild.build());
        } catch (Exception exc) {
            throw new HBaseTableRowException(rowSpec,
                                             "Unexpected failure extracting READ (handle:'"
                                             + readSpec.getHandle()
                                             + "') query results; "
                                             + exc,
                                             exc);
        }
    }
    
    /**
     * TODO
     * @param writeSpec
     * @param writePerformed
     * @throws HBaseTableRowException
     */
    public void assimilate(final WriteOpSpecDefault writeSpec, final boolean writePerformed) throws HBaseTableRowException {
        try {
            storeResult(writeSpec,
                        WriteOpResult
                            .getBuilder()
                            .origin(writeSpec)
                            .mutationPerformed(writePerformed)
                            .build());
        } catch (Exception exc) {
            throw new HBaseTableRowException(writeSpec.getTableRow(),
                                             "Unexpected failure extracting WRITE (handle:'"
                                             + writeSpec.getHandle()
                                             + "') query results; "
                                             + exc,
                                             exc);
        }
    }
    
    /**
     * TODO
     * @param spec
     * @return
     */
    public OpResult<?> getResult(final OperationSpec<?> spec) {
        return this.dataBySpec.get(spec);
    }
    public ReadOpResult getReadResult(final ReadOpSpecFrozen spec) throws ClassCastException {
        return (ReadOpResult) getResult(spec);
    }
    public WriteOpResult getWriteResult(final WriteOpSpecFrozen spec) throws ClassCastException {
        return (WriteOpResult) getResult(spec);
    }

    /**
     * TODO
     * @param handle
     * @return
     */
    public OpResult<?> getResult(final Object handle) {
        return this.dataByHandle.get(handle);
    }
    public ReadOpResult getReadResult(final Object handle) throws ClassCastException {
        return (ReadOpResult) getResult(handle);
    }
    public WriteOpResult getWriteResult(final Object handle) throws ClassCastException {
        return (WriteOpResult) getResult(handle);
    }

    /**
     * TODO
     * @return
     */
    public Map<OperationSpec<?>, OpResult<?>> getResultsBySpec() {
        return Collections.unmodifiableMap(this.dataBySpec);
    }
    /**
     * TODO
     * @return
     */
    public Map<Object, OpResult<?>> getResultsByHandle() {
        return Collections.unmodifiableMap(this.dataByHandle);
    }
    /**
     * TODO
     */
    public OpResultSet() {
        this.dataBySpec = new LinkedHashMap<>();
        this.dataByHandle = new LinkedHashMap<>();
    }
}
