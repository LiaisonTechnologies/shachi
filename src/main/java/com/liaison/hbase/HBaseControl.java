package com.liaison.hbase;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

import com.liaison.hbase.api.NamingStrategy;
import com.liaison.hbase.api.opspec.OperationController;
import com.liaison.hbase.context.DefaultHBaseContext;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseControllerLifecycleException;
import com.liaison.hbase.exception.HBaseInitializationException;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;
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
        final byte[] tableNameFull;
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
        tableNameFull = this.tableNamer.buildName(model.getName(), TEMPORARY_KEY_FOR_STUFF);
        // >>>>> LOG >>>>>
        if (LOG.isTraceEnabled()) {
            LOG.trace("[" + logMethodName + "] full table name: " + Util.toString(tableNameFull));
        }
        // <<<<< log <<<<<
        
        attemptCount = 0;
        tableAdmin = admin.get();
        while ((tbl == null) && (attemptCount < TABLECONNECT_ATTEMPTS_MAX)) {
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
            
            try {
                if (!tableAdmin.tableExists(tableNameFull)) {
                    Util.traceLog(LOG, logMethodName, "table does not exist; creating...");
                    tableDesc = new HTableDescriptor(TableName.valueOf(tableNameFull));
                    model.getFamilies()
                        .entrySet()
                        .stream()
                        .forEachOrdered((famEntry) -> {
                            final Name familyName;
                            final String familyNameStr;
                            final byte[] familyNameBytes;
                            final HColumnDescriptor colFamDesc;
                            
                            familyName = famEntry.getKey();
                            familyNameBytes =
                                familyName.getValue(this.context.getDefensiveCopyStrategy());
                            familyNameStr = familyName.getStr();
                            
                            colFamDesc = new HColumnDescriptor(familyNameBytes);
                            
                            // TODO fix this -- do we need to revert back to a normal loop rather than a stream?
                            //tableDesc.addFamily(colFamDesc);
                            
                            
                            
                            // >>>>> LOG >>>>>
                            if (LOG.isTraceEnabled()) {
                                //LOG.trace("[" + logMethodName + "] added family: " + familyNameStr);
                            }
                            // <<<<< log <<<<<
                        });
                    tableAdmin.createTable(tableDesc);
                    Util.traceLog(LOG, logMethodName, "table created");
                }
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
    
    public HBaseControl() {
        // TODO initialization to reasonable values
        this.tableNamer = null;
        this.context = DefaultHBaseContext.getBuilder().build();
        this.admin = null;
        this.tableSet = null;
    }
}
