/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import com.liaison.hbase.api.request.impl.ColSpecRead;
import com.liaison.hbase.api.request.impl.OperationSpec;
import com.liaison.hbase.api.request.impl.ReadOpSpec;
import com.liaison.hbase.api.request.impl.RowSpec;
import com.liaison.hbase.api.request.impl.TableRowOpSpec;
import com.liaison.hbase.api.request.impl.WriteOpSpec;
import com.liaison.hbase.api.response.ReadOpResult.ReadOpResultBuilder;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.HBaseEmptyCellValueException;
import com.liaison.hbase.exception.HBaseNoCellException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.Util;

public class OpResultSet implements Serializable {
    
    private static final long serialVersionUID = 7900478648783128880L;
    
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
    
    private void populateContent(final ReadOpResultBuilder readResBuild, final ReadOpSpec readSpec, final Result res, final RowSpec<ReadOpSpec> rowSpec, final ColSpecRead<ReadOpSpec> readColSpec) throws HBaseNoCellException, HBaseEmptyCellValueException {
        Datum datum = null;
        byte[] content = null;
        Long contentTS = null;
        FamilyQualifierPair fqp = null;
        
        for (Cell resCell : res.rawCells()) {
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
                 * At the moment, the code assumes that an empty return value (byte arrary of
                 * zero length) and a null return value are equivalent; need to determine if
                 * that is a valid assumption, and change the logic, if not.
                 */
                content = Util.simplify(content);
                /*
                 * TODO
                 * As a follow-on to the previous to-do comment, the code as-is ignores any empty
                 * values, and does not pass them back to the client; should this be changed?
                 */
                if (content != null) {
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
                    readResBuild.add(fqp, datum);
                }
            }
        }
        /*
         * If the FamilyQualifierPair is null, then it means that it was never set, which means
         * that the Result set did not contain any non-null cells. In that case, throw a
         * HBaseNoCellException if the read is non-optional.
         */
        if ((fqp == null) && (!readColSpec.isOptional())) {
            throw new HBaseNoCellException(rowSpec,
                    readColSpec,
                    ("READ (handle:'"
                     + readSpec.getHandle()
                     + "') returned no Cell for table/row "
                     + rowSpec
                     + " and required column "
                     + readColSpec));
        }
        /*
         * If datum is null, then it means that none of the cells which were returned contained
         * non-empty data, so throw an HBaseEmptyCellValueException if the read is non-optional.
         */
        if (datum == null) {
            throw new HBaseEmptyCellValueException(rowSpec,
                                                   readColSpec,
                                                   ("READ (handle:'"
                                                    + readSpec.getHandle()
                                                    + "') returned Cell for table/row "
                                                    + rowSpec
                                                    + " and (required) column "
                                                    + readColSpec
                                                    + " with null/empty content"));
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
    public void assimilate(final ReadOpSpec readSpec, final Result res) throws HBaseTableRowException {
        final ReadOpResultBuilder opResBuild;
        final RowSpec<ReadOpSpec> rowSpec;
        final List<ColSpecRead<ReadOpSpec>> colSpecList;

        rowSpec = readSpec.getTableRow();
        try {
            opResBuild = ReadOpResult.getBuilder().origin(readSpec);
            colSpecList = readSpec.getWithColumn();
            for (ColSpecRead<ReadOpSpec> readColSpec : colSpecList) {
                try {
                    populateContent(opResBuild, readSpec, res, rowSpec, readColSpec);
                } catch (HBaseNoCellException | HBaseEmptyCellValueException exc) {
                    opResBuild.add(readColSpec, exc);
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
    public void assimilate(final WriteOpSpec writeSpec, final boolean writePerformed) throws HBaseTableRowException {
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
    /**
     * TODO
     * @param handle
     * @return
     */
    public OpResult<?> getResult(final Object handle) {
        return this.dataByHandle.get(handle);
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
