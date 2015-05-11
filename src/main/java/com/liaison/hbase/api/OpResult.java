package com.liaison.hbase.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import com.liaison.hbase.api.opspec.ColSpec;
import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.OperationSpec;
import com.liaison.hbase.api.opspec.ReadOpSpec;
import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.dto.Datum;
import com.liaison.hbase.exception.HBaseEmptyCellValueException;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseNoCellException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.Util;

public abstract class OpResult {
    
    private final DefensiveCopyStrategy copyStrategy;
    private final Map<OperationSpec<?>, Map<ColSpec<?, ?>, Datum>> data;
    
    public void assimilate(final ReadOpSpec readSpec, final Result res) throws HBaseNoCellException, HBaseEmptyCellValueException, HBaseTableRowException {
        final Map<ColSpec<?, ?>, Datum> resMap;
        final RowSpec<ReadOpSpec> rowSpec;
        final List<ColSpecRead<ReadOpSpec>> colSpecList;
        Cell resCell;
        byte[] cellFamily;
        byte[] cellQual;
        byte[] content;
        Long contentTS;

        rowSpec = readSpec.getTableRow();
        try {
            resMap = new LinkedHashMap<>();
            colSpecList = readSpec.getWithColumn();
            for (ColSpecRead<ReadOpSpec> readColSpec : colSpecList) {
                /*
                 * re-null content; important in case of a subsequent optional field with no data
                 */
                content = null;
                contentTS = null;
                
                cellFamily = readColSpec.getFamily().getName().getValue(copyStrategy);
                cellQual = readColSpec.getColumn().getName().getValue(copyStrategy);
                
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
                    resMap.put(readColSpec,
                               Datum.of(content,
                                        contentTS.longValue(),
                                        DefensiveCopyStrategy.NEVER));
                }
            }
            this.data.put(readSpec, resMap);
        } catch (HBaseException exc) {
            throw exc;
        } catch (Exception exc) {
            throw new HBaseTableRowException(rowSpec,
                                          "Unexpected failure extracting READ (handle:'') query results; "
                                          + exc,
                                          exc);
        }
    }
    
    public OpResult(final DefensiveCopyStrategy copyStrategy) {
        Util.ensureNotNull(copyStrategy, this, "copyStratgegy", DefensiveCopyStrategy.class);
        this.copyStrategy = copyStrategy;
        this.data = new LinkedHashMap<>();
    }
}
