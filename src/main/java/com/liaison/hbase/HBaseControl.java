package com.liaison.hbase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

import com.liaison.hbase.api.NamingStrategy;
import com.liaison.hbase.api.OpResult;
import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.ColSpecWrite;
import com.liaison.hbase.api.opspec.CondSpec;
import com.liaison.hbase.api.opspec.OperationController;
import com.liaison.hbase.api.opspec.OperationSpec;
import com.liaison.hbase.api.opspec.ReadOpSpec;
import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.WriteOpSpec;
import com.liaison.hbase.context.DefaultHBaseContext;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.NullableValue;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseControllerLifecycleException;
import com.liaison.hbase.exception.HBaseEmptyResultSetException;
import com.liaison.hbase.exception.HBaseInitializationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.ReadUtils;
import com.liaison.hbase.util.Util;

public class HBaseControl extends ThreadLocalResourceAwareHBaseController {

    private static final int TABLECONNECT_ATTEMPTS_MAX = 10;
    private static final long TABLECONNECT_RETRYDELAY_INIT_MS = 10;
    private static final long TABLECONNECT_RETRYDELAY_MAX_MS = 5000;
    private static final int TABLECONNECT_RETRYDELAY_MULTIPLIER = 2;
    
    /**
     * TODO change this, figure out what should be used instead
     */
    private static final String TEMPORARY_KEY_FOR_STUFF = "KEY";
    
    private static final Logger LOG;
    
    static {
        LOG = LoggerFactory.getLogger(HBaseControl.class);
    }
    
    /**
     * TODO change key generic type
     */
    private final NamingStrategy<String, byte[]> tableNamer;
    
    private final HBaseContext context;
    private final ThreadLocal<HBaseAdmin> admin;
    private final ConcurrentHashMap<TableModel, ThreadLocal<HTable>> tableSet;
    
    private static final void createTableFromModel(final HBaseAdmin tableAdmin, final TableModel model, final DefensiveCopyStrategy dcs) throws IOException {
        final String logMethodName;
        final HTableDescriptor tableDesc;
        final byte[] tableNameBytes;
        
        // >>>>> LOG >>>>>
        if (LOG.isTraceEnabled()) {
            logMethodName = "generateTableDesc(model=" + model + ")";
            LOG.trace(">>> " + logMethodName);
        } else {
            logMethodName = null;
        }
        // <<<<< log <<<<<

        tableNameBytes = model.getName().getValue(dcs);
        
        if (!tableAdmin.tableExists(tableNameBytes)) {
            Util.traceLog(LOG, logMethodName, "table does not exist; creating...");
            tableDesc = new HTableDescriptor(TableName.valueOf(tableNameBytes));
            model.getFamilies()
                .entrySet()
                .stream()
                .forEachOrdered((famEntry) -> {
                    final Name familyName;
                    final String familyNameStr;
                    final byte[] familyNameBytes;
                    final HColumnDescriptor colFamDesc;
                    
                    familyName = famEntry.getKey();
                    familyNameBytes = familyName.getValue(dcs);
                    familyNameStr = familyName.getStr();
                    
                    colFamDesc = new HColumnDescriptor(familyNameBytes);
                    tableDesc.addFamily(colFamDesc);
                    
                    // >>>>> LOG >>>>>
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("[" + logMethodName + "] added family: " + familyNameStr);
                    }
                    // <<<<< log <<<<<
                });
            tableAdmin.createTable(tableDesc);
            Util.traceLog(LOG, logMethodName, "table created");
        }
    }
    
    /**
     * Establish a connection to the HBase table with the given name and set of column families. If
     * the table does not yet exist, create it.
     * 
     * TODO: This method of connecting to HBase tables is deprecated as of 0.99.0, and has been
     * replaced by a new method (previously unavailable) wherein a Connection is established first,
     * then a Table reference is retrieved from the Connection. (See: https://hbase.apache.org/
     * apidocs/org/apache/hadoop/hbase/client/HTable.html#HTable%28org.apache.hadoop.conf.
     * Configuration,%20byte[]%29 -AND- https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/
     * client/Connection.html#getTable%28org.apache.hadoop.hbase.TableName%29)
     * 
     * TODO: Investigate using an ExecutorService to pool connections? (see: https://hbase.apache.
     * org/apidocs/org/apache/hadoop/hbase/client/HTable.html#HTable%28org.apache.hadoop.conf.
     * Configuration,%20byte[],%20java.util.concurrent.ExecutorService%29 -OR- https://hbase.
     * apache.org/apidocs/org/apache/hadoop/hbase/client/Connection.html#getTable%28org.apache.
     * hadoop.hbase.TableName,%20java.util.concurrent.ExecutorService%29)
     * 
     * @param tableName
     * @param columnFamilies String array indicating the column families which exist in the named table
     * @return
     * @throws TokenManagerInitializationException
     */
    private HTable connectToTable(final TableModel model) {
        String logMsg;
        String logMethodName = null;
        final byte[] tableNameBytes;
        final String tableNameStr;
        final HBaseAdmin tableAdmin;
        HTableDescriptor tableDesc;
        byte[] colFam;
        HTable tbl = null;
        int attemptCount;
        long retryDelay = -1;
        Exception lastException = null;
        
        // >>>>> LOG >>>>>
        if (LOG.isTraceEnabled()) {
            logMethodName = "connectToTable(model=" + model + ")";
            LOG.trace(">>> " + logMethodName);
        }
        // <<<<< log <<<<<

        tableNameBytes = model.getName().getValue(this.context.getDefensiveCopyStrategy());
        tableNameStr = model.getName().getStr();
        // >>>>> LOG >>>>>
        if (LOG.isTraceEnabled()) {
            LOG.trace("[" + logMethodName + "] full table name: " + model.getName());
        }
        // <<<<< log <<<<<
        
        attemptCount = 0;
        tableAdmin = admin.get();
        while ((tbl == null) && (attemptCount < TABLECONNECT_ATTEMPTS_MAX)) {
            /*
             * If this is the first attempt, proceed immediately. Otherwise, delay by the current
             * exponential-backoff delay interval, then apply the exponential backoff for the next
             * interval (if it becomes necessary)
             */
            if (attemptCount == 0) {
                retryDelay = TABLECONNECT_RETRYDELAY_INIT_MS;
            } else {
                // >>>>> LOG >>>>>
                if (LOG.isDebugEnabled()) {
                    logMsg = "Table '" + model.getName() + "' connection attempt "
                             + attemptCount + "/" + TABLECONNECT_ATTEMPTS_MAX
                             + " failed; retrying in " + retryDelay + "ms...";
                    Util.traceLog(LOG, logMethodName, logMsg);
                }
                // <<<<< log <<<<<
                try {
                    Strand.sleep(retryDelay);
                } catch (InterruptedException iExc) {
                    // ignore
                } catch (SuspendExecution quasarInstrumentationExcNeverThrown) {
                    throw new AssertionError(quasarInstrumentationExcNeverThrown);
                }
                retryDelay *= TABLECONNECT_RETRYDELAY_MULTIPLIER;
                retryDelay = Math.min(TABLECONNECT_RETRYDELAY_MAX_MS, retryDelay);
            }
            attemptCount++;
            
            // >>>>> LOG >>>>>
            if (LOG.isDebugEnabled()) {
                logMsg = "Table '" + model.getName() + "' connection attempt "
                         + attemptCount + "/" + TABLECONNECT_ATTEMPTS_MAX
                         + " starting...";
                Util.traceLog(LOG, logMethodName, logMsg);
            }
            // <<<<< log <<<<<
            
            /*
             * If a table with the name given by the model does not yet exist, then generate a
             * descriptor corresponding to that table name, then create it. Then (regardless of
             * whether or not table creation was necessary in the previous step), return an HTable
             * handle for the table.
             */
            try {
                createTableFromModel(tableAdmin, model, getContext().getDefensiveCopyStrategy());
                tbl =
                    new HTable(context.getHBaseConfiguration(),
                               model.getName().getValue(this.context.getDefensiveCopyStrategy()));
            } catch (IOException | IllegalArgumentException exc) {
                // >>>>> LOG >>>>>
                if (LOG.isDebugEnabled()) {
                    logMsg = 
                        "Failed to establish connection to table "
                        + model.getName()
                        + "; "
                        + exc.toString();
                    Util.traceLog(LOG, logMethodName, logMsg, exc);
                }
                // <<<<< log <<<<<
                lastException = exc;
            }
        }
        if (tbl == null) {
            logMsg = "Failed to establish connection to table "
                     + tableNameStr
                     + "; retries exhausted. Last exception was: "
                     + lastException.toString();
            LOG.error(logMsg);
            throw new HBaseInitializationException(logMsg, lastException);
        } else {
            if (LOG.isDebugEnabled()) {
                logMsg = "Connected to table: '" + tableNameStr + "'";
                Util.traceLog(LOG, logMethodName, logMsg);
            }
        }

        try {
            // >>>>> LOG >>>>>
            if (LOG.isTraceEnabled()) {
                LOG.trace("[" + logMethodName + "] Adding to list of all-thread closeable resources: "
                          + tbl
                          + "...");
            }
            // <<<<< log <<<<<
            addCloseableResource(tbl);
            // >>>>> LOG >>>>>
            if (LOG.isTraceEnabled()) {
                LOG.trace("[" + logMethodName + "] Added to list of all-thread closeable resources: "
                          + tbl
                          + " (resource list size: "
                          + countCloseableResources()
                          + ")");
            }
            // <<<<< log <<<<<
        } catch (HBaseControllerLifecycleException vclExc) {
            logMsg = "Failed to establish connection to table "
                     + tableNameStr
                     + "; controller is in a state of destruction/shutdown: "
                     + vclExc.toString();
            LOG.error(logMsg, vclExc);
            throw new HBaseInitializationException(logMsg, vclExc);
        }
        
        // >>>>> LOG >>>>>
        if (LOG.isTraceEnabled()) { LOG.trace("<<< " + logMethodName + ": " + tbl); }
        // <<<<< log <<<<<
        
        return tbl;
    }
    
    public HBaseContext getContext() {
        return this.context;
    }
    
    public OperationController now() {
        return new OperationController(this, this.context);
    }
    
    private static void verifyStateForExec(final OperationSpec<?> opSpec) throws IllegalStateException {
        if (!opSpec.isFrozen()) {
            throw new IllegalStateException(opSpec.getClass().getSimpleName()
                                            + " must be frozen before spec may be executed"); 
        }
    }
    
    public OpResult exec(final ReadOpSpec readSpec) throws IllegalArgumentException {
        final DefensiveCopyStrategy dcs;
        final RowSpec<?> tableRowSpec;
        final HTable readFromTable;
        final Get readGet;
        final List<ColSpecRead<ReadOpSpec>> colReadList;
        FamilyModel colFam;
        QualModel colQual;
        final Result res;
        
        verifyStateForExec(readSpec);
        dcs = this.context.getDefensiveCopyStrategy();
        
        //TODO: major error handling, null-checking, etc.
        try {
            tableRowSpec = readSpec.getFromTableRow();
            readFromTable = this.tableSet.get(tableRowSpec.getTable()).get();
            readGet = new Get(tableRowSpec.getRowKey().getValue(dcs));
            ReadUtils.applyTS(readGet, readSpec);
            colReadList = readSpec.getWithColumn();
            if (colReadList != null) {
                for (ColSpecRead<ReadOpSpec> colSpec : colReadList) {
                    colFam = colSpec.getFamily();
                    colQual = colSpec.getColumn();
                    if (colFam != null) {
                        if (colQual != null) {
                            readGet.addColumn(colFam.getName().getValue(dcs),
                                              colQual.getName().getValue(dcs));
                        } else {
                            readGet.addFamily(colFam.getName().getValue(dcs));
                        }
                    }
                }
            }
            res = readFromTable.get(readGet);
            if ((res == null) || (res.isEmpty())) {
                throw new HBaseEmptyResultSetException(tableRowSpec.getRowKey(), colReadList, "no result set");
            }
        } catch (Exception temporary) {
            // TODO
        }
        
        //TODO
        return null;
    }
    
    public OpResult exec(final WriteOpSpec writeSpec) throws IllegalArgumentException, IllegalStateException {
        final DefensiveCopyStrategy dcs;
        final RowSpec<?> tableRowSpec;
        final HTable writeToTable;
        final List<ColSpecWrite<WriteOpSpec>> colWriteList;
        Long writeTS;
        final CondSpec<?> condition;
        final NullableValue condPossibleValue;
        RowKey rowKey;
        FamilyModel fam;
        QualModel qual;
        final Put writePut;
        
        verifyStateForExec(writeSpec);
        
        dcs = this.context.getDefensiveCopyStrategy();
        
        try {
            tableRowSpec = writeSpec.getOnTableRow();
            writeToTable = this.tableSet.get(tableRowSpec.getTable()).get();
            writePut = new Put(tableRowSpec.getRowKey().getValue(dcs));
            
            colWriteList = writeSpec.getWithColumn();
            if (colWriteList != null) {
                for (ColSpecWrite<WriteOpSpec> colWrite : colWriteList) {
                    writeTS = colWrite.getTs();
                    if (writeTS == null) {
                        writePut.add(colWrite.getFamily().getName().getValue(dcs),
                                     colWrite.getColumn().getName().getValue(dcs),
                                     colWrite.getValue().getValue(dcs));
                    } else {
                        writePut.add(colWrite.getFamily().getName().getValue(dcs),
                                     colWrite.getColumn().getName().getValue(dcs),
                                     writeTS.longValue(),
                                     colWrite.getValue().getValue(dcs));
                    }
                }
            }
            
            condition = writeSpec.getGivenCondition();
            if (condition != null) {
                condPossibleValue = condition.getValue();
                rowKey = condition.getRowKey();
                fam = condition.getFamily();
                qual = condition.getColumn();
                
                /*
                 * It's okay to use NullableValue#getValue here without disambiguating Value vs.
                 * Empty, as both are immutable, and the constructor for the former enforces that
                 * getValue must return NON-NULL, and the constructor for the latter enforces that
                 * getValue must return NULL. Thus, getValue returns what checkAndPut needs in
                 * either case.
                 */
                writeToTable.checkAndPut(rowKey.getValue(dcs),
                                         fam.getName().getValue(dcs),
                                         qual.getName().getValue(dcs),
                                         condPossibleValue.getValue(dcs),
                                         writePut);
            } else {
                writeToTable.put(writePut);
            }
        } catch (Exception temporary) {
            // TODO
        }
        return null;
    }
    
    public HBaseControl() {
        // TODO initialization to reasonable values
        this.tableNamer = null;
        this.context = DefaultHBaseContext.getBuilder().build();
        this.admin = null;
        this.tableSet = null;
    }
}
