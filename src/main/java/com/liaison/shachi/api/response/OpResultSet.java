/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liaison.shachi.api.response;

import com.liaison.javabasics.logging.JitLog;
import com.liaison.javabasics.serialization.BytesUtil;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.api.request.frozen.ColSpecReadFrozen;
import com.liaison.shachi.api.request.frozen.ReadOpSpecFrozen;
import com.liaison.shachi.api.request.frozen.WriteOpSpecFrozen;
import com.liaison.shachi.api.request.impl.ColSpecRead;
import com.liaison.shachi.api.request.impl.OperationSpec;
import com.liaison.shachi.api.request.impl.ReadOpSpecDefault;
import com.liaison.shachi.api.request.impl.RowSpec;
import com.liaison.shachi.api.request.impl.TableRowOpSpec;
import com.liaison.shachi.api.request.impl.WriteOpSpecDefault;
import com.liaison.shachi.api.response.ReadOpResult.ReadOpResultBuilder;
import com.liaison.shachi.dto.Datum;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.dto.ParsedVersionQualifier;
import com.liaison.shachi.dto.SpecCellResultSet;
import com.liaison.shachi.exception.HBaseNoCellException;
import com.liaison.shachi.exception.HBaseTableRowException;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.util.HBaseUtil;
import com.liaison.shachi.util.SpecUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpResultSet implements Serializable {
    
    private static final long serialVersionUID = 7900478648783128880L;

    private static final JitLog LOG;

    static {
        LOG = new JitLog(OpResultSet.class);
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

    private ParsedVersionQualifier extractVersion(final ColSpecReadFrozen colSpec, final byte[] qualBytes, final long ts) {
        return
            HBaseUtil
                .parseQualifierSeparateVersion(qualBytes,
                                               ts,
                                               SpecUtil.determineVersioningScheme(colSpec));
    }

    private Datum buildDatum(final byte[] content, final long contentTS, final ParsedVersionQualifier pvq) {
        final Datum.Builder datumBuild;

        datumBuild = Datum.with().value(content, DefensiveCopyStrategy.ALWAYS).ts(contentTS);
        if (pvq.hasVersion()) {
            datumBuild.version(pvq.getVersion().longValue());
        }
        return datumBuild.build();
    }

    private void populateContentForCell(final ReadOpResultBuilder readResBuild, final ReadOpSpecDefault readSpec, final Cell resCell, final int cellIndex, final int cellTotalCount, final String logMethodName) {
        Datum datum = null;
        byte[] content;
        final byte[] qualBytes;
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
            if (fqp == null) {
                qualBytes = new byte[0];
            } else {
                qualBytes = fqp.getColumn().getName().getValue(DefensiveCopyStrategy.ALWAYS);
            }
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
                 * For any column specifications which are associated with this data cell based
                 * upon the *combination* of family and qualifier (i.e. column specs which
                 * specified both), add this data cell to the result list for the column spec.
                 */
                for (ColSpecReadFrozen colSpec : readSpec.getColumnAssoc(fqp)) {
                    datum =
                        buildDatum(content,
                                   contentTS,
                                   extractVersion(colSpec, qualBytes, contentTS));
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
                 * upon column family *only* (i.e. column specs which are reading from the full
                 * family), add this data cell to the result list for the column spec.
                 */
                for (ColSpecReadFrozen colSpec : readSpec.getColumnAssoc(fqp.getFamily())) {
                    datum =
                        buildDatum(content,
                                   contentTS,
                                   extractVersion(colSpec, qualBytes, contentTS));
                    addToResultBuilderIndexedToColumn(readResBuild,
                                                      colSpec,
                                                      datum,
                                                      fqp,
                                                      cellIndex,
                                                      cellTotalCount,
                                                      fqp.getFamily(),
                                                      logMethodName);
                }

                /*
                 * For any column specifications which are associated with this data cell based
                 * upon (a) column family and (b) column qualifier being within a ColumnRange
                 * associated with this column, add this data cell to the result list for the
                 * column spec. (The getColumnRangeAssoc will check the given family-qualifier pair
                 * against the defined column ranges.)
                 */
                for (ColSpecReadFrozen colSpec : readSpec.getColumnRangeAssoc(fqp)) {
                    datum =
                        buildDatum(content,
                                   contentTS,
                                   extractVersion(colSpec, qualBytes, contentTS));
                    addToResultBuilderIndexedToColumn(readResBuild,
                                                      colSpec,
                                                      datum,
                                                      fqp,
                                                      cellIndex,
                                                      cellTotalCount,
                                                      fqp,
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
     * @param resList
     * @throws HBaseTableRowException
     */
    public void assimilate(final ReadOpSpecDefault readSpec, final Iterable<Result> resList) throws HBaseTableRowException {
        String logMsg;
        final ReadOpResultBuilder opResBuild;
        final RowSpec<ReadOpSpecDefault> rowSpec;
        SpecCellResultSet readColSpecResult;

        rowSpec = readSpec.getTableRow();
        try {
            opResBuild = ReadOpResult.getBuilder().origin(readSpec);
            for (Result res : resList) {
                populateContent(opResBuild, readSpec, res);
            }

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
