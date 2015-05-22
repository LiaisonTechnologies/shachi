package com.liaison.hbase.util;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;

import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.HBaseInitializationException;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.resmgr.ResourceConnectTolerance;

public final class HBaseUtil {
    
    private static final LogMeMaybe LOG;
    
    public static final void createTableFromModel(final HBaseAdmin tableAdmin, final TableModel model, Name tableName, final DefensiveCopyStrategy dcs) throws IOException {
        final String logMethodName;
        final HTableDescriptor tableDesc;
        final byte[] tableNameBytes;
        
        logMethodName =
            LOG.enter(()->"generateTableDesc(model=",
                      ()->model,
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
    
    private static HTable attemptConnect(final String logMethodName, final HBaseContext context, final HBaseAdmin tableAdmin, final TableModel model, final Name tableName, final int attemptCount) throws IOException, IllegalArgumentException {
        final ResourceConnectTolerance rct;
        final DefensiveCopyStrategy dcs;
        
        rct = context.getResourceConnectTolerance();
        dcs = context.getDefensiveCopyStrategy();
        
        LOG.debug(logMethodName,
                  ()->"Table '",
                  ()->tableName,
                  ()->"' connection attempt ",
                  ()->Integer.toString(attemptCount),
                  ()->"/",
                  ()->Integer.toString(rct.getAttemptsMax()),
                  ()->" starting...");
        /*
         * If a table with the name given by the model does not yet exist, then generate a
         * descriptor corresponding to that table name, then create it. Then (regardless of
         * whether or not table creation was necessary in the previous step), return an HTable
         * handle for the table.
         */
        HBaseUtil.createTableFromModel(tableAdmin, model, tableName, dcs);
        return new HTable(context.getHBaseConfiguration(), tableName.getValue(dcs));
    }
    
    private static long sleepIfOnRetry(final String logMethodName, final Name tableName, final int attemptCount, final long currentRetryDelay, final ResourceConnectTolerance rct) {
        long nextRetryDelay;
        
        /*
         * If this is the first attempt, proceed immediately. Otherwise, delay by the current
         * exponential-backoff delay interval, then apply the exponential backoff for the next
         * interval (if it becomes necessary)
         */
        if (attemptCount == 0) {
            nextRetryDelay = rct.getRetryDelayInit();
        } else {
            LOG.debug(logMethodName,
                      ()->"Table '",
                      ()->tableName,
                      ()->"' connection attempt ",
                      ()->Integer.toString(attemptCount),
                      ()->"/",
                      ()->Integer.toString(rct.getAttemptsMax()),
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
            nextRetryDelay = currentRetryDelay * rct.getRetryDelayMultiplier();
            nextRetryDelay = Math.min(rct.getRetryDelayMax(), nextRetryDelay);
        }
        return nextRetryDelay;
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
    public static HTable connectToTable(final HBaseContext context, final HBaseAdmin admin, final TableModel model) throws HBaseInitializationException {
        String logMsg;
        final String logMethodName;
        final Name tableName;
        final String tableNameStr;
        final ResourceConnectTolerance rct;
        HTable tbl = null;
        int attemptCount;
        long retryDelay = -1;
        Exception lastException = null;
        
        Util.ensureNotNull(context, "HBaseUtil#connectToTable", "context", HBaseContext.class);
        Util.ensureNotNull(admin, "HBaseUtil#connectToTable", "admin", HBaseAdmin.class);
        Util.ensureNotNull(model, "HBaseUtil#connectToTable", "model", TableModel.class);
        
        logMethodName
            = LOG.enter(()->"connectToTable(context=",
                        ()->context,
                        ()->"model=",
                        ()->model,
                        ()->")");

        rct = context.getResourceConnectTolerance();
        tableName = context.getTableNamingStrategy().generate(model);
        tableNameStr = tableName.getStr();

        LOG.trace(logMethodName, ()->"full table name: ", ()->tableName);
        
        attemptCount = 0;
        retryDelay = -1;
        while ((tbl == null) && (attemptCount < rct.getAttemptsMax())) {
            retryDelay = sleepIfOnRetry(logMethodName, tableName, attemptCount, retryDelay, rct);
            attemptCount++;
            try {
                tbl = attemptConnect(logMethodName, context, admin, model, tableName, attemptCount);
            } catch (IOException | IllegalArgumentException exc) {
                LOG.debug(exc,
                          logMethodName,
                          ()->"Failed to establish connection to table '",
                          ()->tableName,
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
        
        LOG.leave(logMethodName);
        return tbl;
    }
    
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
    public static HBaseAdmin connectAdmin(final HBaseContext context) throws HBaseInitializationException {
        String logMsg;
        final String logMethodName;
        final HBaseAdmin admin;
        
        logMethodName = LOG.enter(()->"connectAdmin");
        try {
            admin = new HBaseAdmin(context.getHBaseConfiguration());
        } catch (IOException ioExc) {
            logMsg = "Failed to establish admin connection";
            LOG.error(ioExc, ()->logMsg);
            throw new HBaseInitializationException(logMsg, ioExc);
        }
        LOG.leave(logMethodName);
        return admin;
    }
    
    static {
        LOG = new LogMeMaybe(HBaseUtil.class);
    }

    private HBaseUtil() { }
}
