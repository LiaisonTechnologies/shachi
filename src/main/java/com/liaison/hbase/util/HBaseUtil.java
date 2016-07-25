/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.liaison.commons.Util;
import com.liaison.commons.log.LogMeMaybe;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.ParsedVersionQualifier;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseInitializationException;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.model.VersioningModel;
import com.liaison.hbase.resmgr.ResourceConnectTolerance;
import com.liaison.serialization.BytesUtil;
import com.liaison.serialization.DefensiveCopyStrategy;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.file.tfile.Utils;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;

import java.io.IOException;
import java.util.Arrays;

public final class HBaseUtil {

    public static final byte[] DELIM_BYTES = {0};

    private static final int BYTES_PER_INT;
    private static final int BYTES_PER_LONG;

    private static final LogMeMaybe LOG;
    private static final HashFunction HASH_MURMUR3_32;

    private static byte[] buildSaltedRowKeyValue(final byte[] salt, final int saltOffset, final int saltLen, final byte[] rkBytes) {
        final byte[] saltedRKBytes;

        // build a new byte array to contain the RowKey content, the salt value, and a delimiter to
        // be placed between them (DELIM_BYTES)
        saltedRKBytes = new byte[rkBytes.length + saltLen + DELIM_BYTES.length];
        // copy the specified sub-array of the salt into the beginning of the new array
        System.arraycopy(salt, saltOffset, saltedRKBytes, 0, saltLen);
        // copy the delimiter into the new array following the salt
        System.arraycopy(DELIM_BYTES, 0, saltedRKBytes, saltLen, DELIM_BYTES.length);
        // copy the original RowKey contents to fill out the rest of the new array
        System.arraycopy(rkBytes,
                         0,
                         saltedRKBytes,
                         (saltLen + DELIM_BYTES.length),
                         rkBytes.length);

        return saltedRKBytes;
    }

    public static byte[] saltRowKeyMurmur3_32(final RowKey rk) {
        final byte[] rkBytes;
        final HashCode hash;
        final byte[] hashBytes;

        rkBytes = rk.getValue(DefensiveCopyStrategy.NEVER);
        hash = HASH_MURMUR3_32.hashBytes(rkBytes);
        hashBytes = hash.asBytes();
        return buildSaltedRowKeyValue(hashBytes,
                                      0,
                                      Math.min(hashBytes.length, BYTES_PER_INT),
                                      rkBytes);
    }

    public static ParsedVersionQualifier parseQualifierSeparateVersion(final byte[] storedQual, final long storedTS, final VersioningModel model) throws IllegalArgumentException, ArithmeticException {
        String logMsg;
        final Long rawVersionIndicatorNumber;
        final int rawVersionIndicatorNumberByteArrayIndex;
        final int qualWithoutVersionLength;
        final Long versionNumber;
        final byte[] qualWithoutVersion;

        Util.ensureNotNull(storedQual,
                           "parseQualifierSeparateVersion",
                           "storedQual",
                           byte[].class);

        // Obtain the raw "version-indicator number", i.e. the actual number written to HBase to
        // indicate the version number. Depending on the versioning model used (as driven by HBase-
        // specific use cases), this indicator number may or may not require transformation in
        // order to derive the *actual* version number, as specified through the API when the cell
        // was originally written (see following if-block after this one)
        if (VersioningModel.isQualifierBased(model)) {
            // If using qualifier-based versioning, the version indicator number will be appended
            // as 8 bytes on the end of the qualifier
            rawVersionIndicatorNumberByteArrayIndex = storedQual.length - BYTES_PER_LONG;
            // The calculated version number must start at an index AT LEAST equal to the delimiter
            // length, if the versioned qualifier was generated according to the rules in the
            // #appendVersionToQual method.
            if (rawVersionIndicatorNumberByteArrayIndex < DELIM_BYTES.length) {
                logMsg =
                    "Failed to extract version number from the qualifier as specified by the "
                    + "versioning model: "
                    + model
                    + "; malformed stored (with-version) qualifier value";
                throw new IllegalArgumentException(logMsg);
            }
            // Extract the bytes for the version-indicator number from the qualifier and convert to
            // a long
            rawVersionIndicatorNumber =
                Long.valueOf(BytesUtil.toLong(storedQual,
                                              rawVersionIndicatorNumberByteArrayIndex));

        } else if (VersioningModel.isTimestampBased(model)) {
            rawVersionIndicatorNumber = Long.valueOf(storedTS);
            qualWithoutVersion = storedQual;
        } else {
            rawVersionIndicatorNumber = null;
            qualWithoutVersion = storedQual;
        }

        // For versioning schemes where the version number is "inverted" (i.e. subtracted from
        // Long.MAX_VALUE) in order to ensure the proper HBase-ordering semantics for the scheme,
        // reverse the inversion by subtracting it again from Long.MAX_VALUE.
        if ((rawVersionIndicatorNumber != null) && (VersioningModel.isInverting(model))) {
            if (rawVersionIndicatorNumber.longValue() < 0) {
                logMsg = "Overflow when computing version number: versioning model ("
                         + model
                         + ") requires subtraction from Long.MAX_VALUE for specified ordering, "
                         + "but initial version number is negative, and Long.MAX_VALUE - version "
                         + "> Long.MAX_VALUE for version < 0";
                throw new ArithmeticException(logMsg);
            }
            versionNumber = Long.valueOf(Long.MAX_VALUE - rawVersionIndicatorNumber.longValue());
        } else {
            versionNumber = rawVersionIndicatorNumber;
        }
    }

    public static byte[] appendVersionToQual(final byte[] original, final long version, final VersioningModel model) throws ArithmeticException {
        String logMsg;
        final byte[] versionBytes;

        if (model == VersioningModel.QUALIFIER_LATEST) {
            if (version < 0) {
                logMsg = "Overflow when computing version number: versioning model ("
                         + VersioningModel.QUALIFIER_LATEST
                         + ") requires subtraction from Long.MAX_VALUE for most-recent-first "
                         + "ordering, but initial version number is negative, and Long.MAX_VALUE -"
                         + " version > Long.MAX_VALUE for version < 0";
                throw new ArithmeticException(logMsg);
            }
            versionBytes = BytesUtil.toBytes(Long.MAX_VALUE - version);
        } else {
            versionBytes = BytesUtil.toBytes(version);
        }
        /*
         * TODO: make the format for a field identifier which includes a version configurable,
         * rather than using the original + 0-byte + version format for all cases
         */
        return BytesUtil.concat(original, DELIM_BYTES, versionBytes);
    }

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
                        ()->",model=",
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
     * @throws HBaseInitializationException If an {@link IOException} occurs during
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
        final int anyInt;
        final long anyLong;

        anyInt = 0;
        anyLong = 0L;

        LOG = new LogMeMaybe(HBaseUtil.class);
        HASH_MURMUR3_32 = Hashing.murmur3_32();
        BYTES_PER_INT = BytesUtil.toBytes(anyInt).length;
        BYTES_PER_LONG = BytesUtil.toBytes(anyLong).length;
    }

    private HBaseUtil() { }
}
