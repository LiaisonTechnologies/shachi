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

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

import com.liaison.hbase.api.opspec.ColSpecRead;
import com.liaison.hbase.api.opspec.ColSpecWrite;
import com.liaison.hbase.api.opspec.CondSpec;
import com.liaison.hbase.api.opspec.OperationController;
import com.liaison.hbase.api.opspec.OperationSpec;
import com.liaison.hbase.api.opspec.ReadOpSpec;
import com.liaison.hbase.api.opspec.RowSpec;
import com.liaison.hbase.api.opspec.WriteOpSpec;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.NullableValue;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseControllerLifecycleException;
import com.liaison.hbase.exception.HBaseEmptyResultSetException;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseInitializationException;
import com.liaison.hbase.exception.HBaseMultiColumnException;
import com.liaison.hbase.exception.HBaseNoSuchTableException;
import com.liaison.hbase.exception.HBaseRuntimeException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.util.DefensiveCopyStrategy;
import com.liaison.hbase.util.LogMeMaybe;
import com.liaison.hbase.util.ReadUtils;
import com.liaison.hbase.util.Util;

public class HBaseControl extends ThreadLocalResourceAwareHBaseController {

    public final class HBaseDelegate {
        
        public Result exec(final ReadOpSpec readSpec) throws IllegalArgumentException, HBaseException, HBaseRuntimeException {
            final DefensiveCopyStrategy dcs;
            final RowSpec<?> tableRowSpec;
            
            final HTable readFromTable;
            final Get readGet;
            final List<ColSpecRead<ReadOpSpec>> colReadList;
            FamilyModel colFam;
            QualModel colQual;
            final Result res;
            
            // Ensure that the spec contains all required attributes for a READ operation
            verifyStateForExec(readSpec);
            
            dcs = HBaseControl.this.context.getDefensiveCopyStrategy();
            
            //TODO: major error handling, null-checking, etc.
            try {
                tableRowSpec = readSpec.getTableRow();
                readFromTable = obtainTable(tableRowSpec.getTable());
                readGet = new Get(tableRowSpec.getRowKey().getValue(dcs));
                try {
                    ReadUtils.applyTS(readGet, readSpec);
                } catch (IOException ioExc) {
                    throw new HBaseTableRowException(tableRowSpec,
                                                  "Failed to apply timestamp cond to READ per spec: "
                                                  + readSpec + "; " + ioExc,
                                                  ioExc);
                }
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
                try {
                    res = readFromTable.get(readGet);
                } catch (IOException ioExc) {
                    throw new HBaseMultiColumnException(tableRowSpec,
                                                        colReadList,
                                                        "READ failed; " + ioExc,
                                                        ioExc);
                }
                if ((res == null) || (res.isEmpty())) {
                    throw new HBaseEmptyResultSetException(tableRowSpec,
                                                           colReadList,
                                                           "READ failed; null/empty result set");
                }
            } catch (HBaseException | HBaseRuntimeException exc) {
                throw exc;
            } catch (Exception exc) {
                throw new HBaseRuntimeException("Unexpected failure during READ operation ("
                                                + readSpec
                                                + "): "
                                                + exc.toString(),
                                                exc);
            }
            return res;
        }
        
        public boolean exec(final WriteOpSpec writeSpec) throws IllegalArgumentException, IllegalStateException, HBaseException, HBaseRuntimeException {
            final DefensiveCopyStrategy dcs;
            final RowSpec<WriteOpSpec> tableRowSpec;
            final HTable writeToTable;
            final List<ColSpecWrite<WriteOpSpec>> colWriteList;
            Long writeTS;
            final CondSpec<?> condition;
            final NullableValue condPossibleValue;
            RowKey rowKey;
            FamilyModel fam;
            QualModel qual;
            final Put writePut;
            boolean writeCompleted;
            
            writeCompleted = false;
            
            // Ensure that the spec contains all required attributes for a READ operation
            verifyStateForExec(writeSpec);
            
            dcs = HBaseControl.this.context.getDefensiveCopyStrategy();
            
            try {
                tableRowSpec = writeSpec.getTableRow();
                writeToTable = obtainTable(tableRowSpec.getTable());
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
                try {
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
                        writeCompleted =
                            writeToTable.checkAndPut(rowKey.getValue(dcs),
                                                     fam.getName().getValue(dcs),
                                                     qual.getName().getValue(dcs),
                                                     condPossibleValue.getValue(dcs),
                                                     writePut);
                    } else {
                        writeToTable.put(writePut);
                        writeCompleted = true;
                    }
                } catch (IOException ioExc) {
                    throw new HBaseMultiColumnException(tableRowSpec,
                                                        colWriteList,
                                                        ("WRITE failure"
                                                         + ((condition == null)
                                                            ?"; "
                                                            :" (with condition: " + condition + "); ")
                                                         + ioExc),
                                                        ioExc);
                }
            } catch (HBaseException | HBaseRuntimeException exc) {
                throw exc;
            } catch (Exception exc) {
                throw new HBaseException("Unexpected failure during WRITE operation ("
                                         + writeSpec
                                         + "): "
                                         + exc.toString(),
                                         exc);
            }
            return writeCompleted;
        }
        
        /**
         * Use a private constructor so that the enclosing HBaseControl instance can control who
         * has access to the delegate (and, consequently, who can execute HBase operations based
         * upon specifications).
         */
        private HBaseDelegate() { }
    }
    
    private static final int TABLECONNECT_ATTEMPTS_MAX = 10;
    private static final long TABLECONNECT_RETRYDELAY_INIT_MS = 10;
    private static final long TABLECONNECT_RETRYDELAY_MAX_MS = 5000;
    private static final int TABLECONNECT_RETRYDELAY_MULTIPLIER = 2;
    
    private static final LogMeMaybe LOG;
    
    static {
        LOG = new LogMeMaybe(HBaseControl.class);
    }
    
    private static final void createTableFromModel(final HBaseAdmin tableAdmin, final TableModel model, Name tableName, final DefensiveCopyStrategy dcs) throws IOException {
        final String logMethodName;
        final HTableDescriptor tableDesc;
        final byte[] tableNameBytes;
        
        logMethodName =
            LOG.enter(()->"generateTableDesc(model=",
                      ()->model.toString(),
                      ()->")");

        if (tableName == null) {
            tableName = model.getName();
        }
        tableNameBytes = tableName.getValue(dcs);
        
        if (!tableAdmin.tableExists(tableNameBytes)) {
            LOG.trace(logMethodName, ()->"table does not exist; creating...");
            tableDesc = new HTableDescriptor(TableName.valueOf(tableNameBytes));
            model.getFamilies()
                 .entrySet()
                 .stream()
                 .forEachOrdered((famEntry) -> {
                     final Name familyName;
                     final byte[] familyNameBytes;
                     final HColumnDescriptor colFamDesc;

                     familyName = famEntry.getKey();
                     familyNameBytes = familyName.getValue(dcs);

                     colFamDesc = new HColumnDescriptor(familyNameBytes);
                     tableDesc.addFamily(colFamDesc);
                     
                     LOG.trace(logMethodName, ()->"added family: ", ()->familyName.getStr());
                 });
            tableAdmin.createTable(tableDesc);
            LOG.trace(logMethodName, ()->"table created");
        }
    }
    
    private static void verifyStateForExec(final OperationSpec<?> opSpec) throws IllegalStateException {
        if (!opSpec.isFrozen()) {
            throw new IllegalStateException(opSpec.getClass().getSimpleName()
                                            + " must be frozen before spec may be executed"); 
        }
    }
    
    private final HBaseContext context;
    private final ThreadLocal<HBaseAdmin> admin;
    private final ConcurrentHashMap<TableModel, ThreadLocal<HTable>> tableSet;
    private final HBaseDelegate delegate;
    
    /**
     * Create a new instance of HBaseAdmin using the configuration assigned to this instance.
     * 
     * TODO: This method of connecting to the HBase admin client is deprecated as of 0.99.0, and
     * has been replaced by a new method (previously unavailable) wherein a Connection is
     * established first, then an Admin reference is retrieved from the Connection. (See:
     * https://hbase.apache.org/apidocs/index.html?org/apache/hadoop/hbase/client/HBaseAdmin.html
     * -AND- https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/Connection.html#
     * getAdmin%28%29)
     * 
     * @return HBaseAdmin instance representing the HBase instance referenced by the configuration
     * @throws TokenManagerInitializationException If an {@link IOException} occurs during
     * establishment of the HBaseAdmin connection.
     */
    private HBaseAdmin connectAdmin() throws HBaseInitializationException {
        String logMsg;
        final String logMethodName;
        final HBaseAdmin admin;
        
        logMethodName = LOG.enter(()->"connectAdmin");
        try {
            admin = new HBaseAdmin(this.context.getHBaseConfiguration());
        } catch (IOException ioExc) {
            logMsg = "Failed to establish admin connection";
            LOG.error(ioExc, ()->logMsg);
            throw new HBaseInitializationException(logMsg, ioExc);
        }
        LOG.leave(logMethodName);
        return admin;
    }
    
    private long sleepIfOnRetry(final String logMethodName, final Name tableName, final int attemptCount, final long currentRetryDelay) {
        long nextRetryDelay;
        
        /*
         * If this is the first attempt, proceed immediately. Otherwise, delay by the current
         * exponential-backoff delay interval, then apply the exponential backoff for the next
         * interval (if it becomes necessary)
         */
        if (attemptCount == 0) {
            nextRetryDelay = TABLECONNECT_RETRYDELAY_INIT_MS;
        } else {
            LOG.debug(logMethodName,
                      ()->"Table '",
                      ()->tableName.toString(),
                      ()->"' connection attempt ",
                      ()->Integer.toString(attemptCount),
                      ()->"/",
                      ()->Integer.toString(TABLECONNECT_ATTEMPTS_MAX),
                      ()->" failed; retrying in ",
                      ()->Long.toString(currentRetryDelay),
                      ()->"ms...");
            try {
                Strand.sleep(currentRetryDelay);
            } catch (InterruptedException iExc) {
                // ignore
            } catch (SuspendExecution quasarInstrumentationExcNeverThrown) {
                throw new AssertionError(quasarInstrumentationExcNeverThrown);
            }
            nextRetryDelay = currentRetryDelay * TABLECONNECT_RETRYDELAY_MULTIPLIER;
            nextRetryDelay = Math.min(TABLECONNECT_RETRYDELAY_MAX_MS, nextRetryDelay);
        }
        return nextRetryDelay;
    }
    
    private HTable attemptConnect(final String logMethodName, final HBaseAdmin tableAdmin, final TableModel model, final Name tableName, final int attemptCount) throws IOException, IllegalArgumentException {
        LOG.debug(logMethodName,
                  ()->"Table '",
                  ()->tableName.toString(),
                  ()->"' connection attempt ",
                  ()->Integer.toString(attemptCount),
                  ()->"/",
                  ()->Integer.toString(TABLECONNECT_ATTEMPTS_MAX),
                  ()->" starting...");
        
        /*
         * If a table with the name given by the model does not yet exist, then generate a
         * descriptor corresponding to that table name, then create it. Then (regardless of
         * whether or not table creation was necessary in the previous step), return an HTable
         * handle for the table.
         */
        createTableFromModel(tableAdmin,
                             model,
                             tableName,
                             getContext().getDefensiveCopyStrategy());
        return
            new HTable(context.getHBaseConfiguration(),
                       tableName.getValue(this.context.getDefensiveCopyStrategy()));
    }
    
    private void registerTableAsCloseableResource(final String logMethodName, final HTable tbl, final String tableNameStr) throws HBaseInitializationException {
        final String logMsg;
        final int closeableResourceCount;

        try {
            LOG.trace(logMethodName,
                      ()->"Adding to list of all-thread closeable resources: ",
                      ()->tbl.toString(),
                      ()->"...");
            addCloseableResource(tbl);
            closeableResourceCount = countCloseableResources();
            LOG.trace(logMethodName,
                      ()->"Added to list of all-thread closeable resources: ",
                      ()->tbl.toString(),
                      ()->" (resource list size: ",
                      ()->Integer.toString(closeableResourceCount),
                      ()->")");
        } catch (HBaseControllerLifecycleException vclExc) {
            logMsg = "Failed to establish connection to table "
                     + tableNameStr
                     + "; controller is in a state of destruction/shutdown: "
                     + vclExc.toString();
            LOG.error(vclExc, logMethodName, ()->logMsg);
            throw new HBaseInitializationException(logMsg, vclExc);
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
     * @throws HBaseInitializationException
     */
    private HTable connectToTable(final TableModel model) throws HBaseInitializationException {
        String logMsg;
        final String logMethodName;
        final Name tableName;
        final String tableNameStr;
        final HBaseAdmin tableAdmin;
        HTable tbl = null;
        int attemptCount;
        long retryDelay = -1;
        Exception lastException = null;
        
        logMethodName
            = LOG.enter(()-> "connectToTable(model=",
                        ()->model.toString(),
                        ()->")");

        tableName = this.context.getTableNamingStrategy().generate(model);
        tableNameStr = tableName.getStr();

        LOG.trace(logMethodName, ()->"full table name: ", ()->tableName.toString());
        
        attemptCount = 0;
        tableAdmin = admin.get();
        retryDelay = -1;
        while ((tbl == null) && (attemptCount < TABLECONNECT_ATTEMPTS_MAX)) {
            retryDelay = sleepIfOnRetry(logMethodName, tableName, attemptCount, retryDelay);
            attemptCount++;
            try {
                tbl = attemptConnect(logMethodName, tableAdmin, model, tableName, attemptCount);
            } catch (IOException | IllegalArgumentException exc) {
                LOG.debug(exc,
                          logMethodName,
                          ()->"Failed to establish connection to table '",
                          ()->tableName.toString(),
                          ()->"'");
                lastException = exc;
            }
        }
        
        if (tbl == null) {
            logMsg = "Failed to establish connection to table "
                     + tableNameStr
                     + "; retries exhausted. Last exception was: "
                     + lastException.toString();
            LOG.error(lastException,
                      logMethodName,
                      ()->logMsg);
            throw new HBaseInitializationException(logMsg, lastException);
        } else {
            LOG.debug(logMethodName,
                      ()->"Connected to table: '",
                      ()->tableNameStr,
                      ()->"'");
        }

        registerTableAsCloseableResource(logMethodName, tbl, tableNameStr);
        
        LOG.leave(logMethodName);
        return tbl;
    }
    
    private HTable obtainTable(final TableModel model) throws HBaseNoSuchTableException, HBaseInitializationException {
        String logMsg;
        ThreadLocal<HTable> tlTable;
        final ThreadLocal<HTable> tlNewTable;

        tlTable = this.tableSet.get(model);
        if (tlTable == null) {
            tlNewTable =
                ThreadLocal.withInitial(() -> {
                    return connectToTable(model);
                });
            tlTable = this.tableSet.putIfAbsent(model, tlNewTable);
            /*
             * In the unlikely event that some other concurrent strand put a ThreadLocal<HTable>
             * into the concurrent map between the initial check and the putIfAbsent, then be sure
             * to return *that* one, rather than the one just created locally (tlNewTable). In most
             * cases, the return should be tlNewTable, though.
             */
            if (tlTable == null) {
                tlTable = tlNewTable;
            }
        }
        if (tlTable == null) {
            logMsg = "No table for model " + model + " exists, and initialization failed";
            throw new HBaseInitializationException(logMsg);
        }
        return tlTable.get();
    }
    
    public HBaseContext getContext() {
        return this.context;
    }
    
    public OperationController begin() {
        return new OperationController(this.delegate, this.context);
    }
    
    public HBaseControl(final HBaseContext context) {
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        this.delegate = new HBaseDelegate();
        this.admin = ThreadLocal.withInitial(() -> {return connectAdmin();});
        this.tableSet = new ConcurrentHashMap<>();
    }
}
