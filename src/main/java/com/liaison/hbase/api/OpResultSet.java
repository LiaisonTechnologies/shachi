package com.liaison.hbase.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.OperationSpec;
import com.liaison.hbase.api.opspec.ReadOpSpec;
import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.TableRowOpSpec;
import com.liaison.hbase.api.opspec.WriteOpSpec;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.exception.HBaseEmptyCellValueException;
import com.liaison.hbase.exception.HBaseNoCellException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.Util;

public class OpResultSet implements Serializable {
    
    private static final long serialVersionUID = 7900478648783128880L;
    
    private final DefensiveCopyStrategy copyStrategy;
    private final Map<OperationSpec<?>, OpResult<?>> dataBySpec;
    private final Map<Object, OpResult<?>> dataByHandle;
    
    private Datum getContent(final ReadOpSpec readSpec, final Result res, final RowSpec<ReadOpSpec> rowSpec, final ColSpecRead<ReadOpSpec> readColSpec) throws HBaseNoCellException, HBaseEmptyCellValueException {
        Datum datum = null;
        byte[] content = null;
        Long contentTS = null;
        final Cell resCell;
        final byte[] cellFamily;
        final byte[] cellQual;
        
        cellFamily = readColSpec.getFamily().getName().getValue(this.copyStrategy);
        cellQual = readColSpec.getColumn().getName().getValue(this.copyStrategy);
        
        resCell = res.getColumnLatestCell(cellFamily, cellQual);
        if (resCell == null) {
            if (!readColSpec.isOptional()) {
                throw new HBaseNoCellException(rowSpec,
                                               readColSpec,
                                               ("READ (handle:'"
                                                + readSpec.getHandle()
                                                + "') returned no Cell for table/row "
                                                + rowSpec
                                                + " and required column "
                                                + readColSpec));
            }
        } else {
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
             * The if predicate here must change to include an explicit check for an empty
             * array *IF* the previous code is modified to remove the empty-array-to-null
             * simplifying transformation.
             */
            if ((content == null) && (!readColSpec.isOptional())) {
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
        }
        return datum;
    }
    
    private <O extends TableRowOpSpec<O>> void storeResult(final O origin, final OpResult<O> opRes) {
        this.dataBySpec.put(origin, opRes);
        this.dataByHandle.put(origin.getHandle(), opRes);
    }
    
    public void assimilate(final ReadOpSpec readSpec, final Result res) throws HBaseTableRowException {
        final OpResult.Builder<ReadOpSpec> opResBuild;
        final RowSpec<ReadOpSpec> rowSpec;
        final List<ColSpecRead<ReadOpSpec>> colSpecList;
        Datum datum;

        rowSpec = readSpec.getTableRow();
        try {
            opResBuild = OpResult.getBuilder(ReadOpSpec.class).origin(readSpec);
            colSpecList = readSpec.getWithColumn();
            for (ColSpecRead<ReadOpSpec> readColSpec : colSpecList) {
                try {
                    datum = getContent(readSpec, res, rowSpec, readColSpec);
                    if (datum != null) {
                        opResBuild.add(readColSpec, datum);
                    }
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
    
    public void assimilate(final WriteOpSpec writeSpec, final boolean writePerformed) throws HBaseTableRowException {
        try {
            storeResult(writeSpec,
                        OpResult
                            .getBuilder(WriteOpSpec.class)
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
    
    public OpResult<?> getResult(final OperationSpec<?> spec) {
        return this.dataBySpec.get(spec);
    }
    public OpResult<?> getResult(final Object handle) {
        return this.dataByHandle.get(handle);
    }
    public Map<OperationSpec<?>, OpResult<?>> getResultsBySpec() {
        return Collections.unmodifiableMap(this.dataBySpec);
    }
    public Map<Object, OpResult<?>> getResultsByHandle() {
        return Collections.unmodifiableMap(this.dataByHandle);
    }
    
    public OpResultSet(final DefensiveCopyStrategy copyStrategy) {
        Util.ensureNotNull(copyStrategy, this, "copyStrategy", DefensiveCopyStrategy.class);
        this.copyStrategy = copyStrategy;
        this.dataBySpec = new LinkedHashMap<>();
        this.dataByHandle = new LinkedHashMap<>();
    }
}
