/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.liaison.commons.DefensiveCopyStrategy;
import com.liaison.commons.Util;
import com.liaison.commons.log.LogMeMaybe;
import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.request.frozen.ColSpecFrozen;
import com.liaison.hbase.api.request.frozen.ColSpecWriteFrozen;
import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.api.request.frozen.ReadOpSpecFrozen;
import com.liaison.hbase.api.request.impl.ColSpecRead;
import com.liaison.hbase.api.request.impl.CondSpec;
import com.liaison.hbase.api.request.impl.LongValueSpec;
import com.liaison.hbase.api.request.impl.OperationControllerDefault;
import com.liaison.hbase.api.request.impl.OperationSpec;
import com.liaison.hbase.api.request.impl.ReadOpSpecDefault;
import com.liaison.hbase.api.request.impl.RowSpec;
import com.liaison.hbase.api.request.impl.WriteOpSpecDefault;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.dto.NullableValue;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.exception.HBaseEmptyResultSetException;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseMultiColumnException;
import com.liaison.hbase.exception.HBaseRuntimeException;
import com.liaison.hbase.exception.HBaseTableRowException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.VersioningModel;
import com.liaison.hbase.resmgr.HBaseResourceManager;
import com.liaison.hbase.resmgr.res.ManagedTable;
import com.liaison.hbase.util.HBaseUtil;
import com.liaison.hbase.util.ReadUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HBaseControl is the main kernel of functionality for the HBase Client, and as the default
 * implementation of HBaseStart, is the primary starting point for use of the fluent API.
 * 
 * Per the contract for {@link HBaseStart#begin()}, {@link HBaseControl#begin()} starts the API
 * operation-specification generation process by which clients specify HBase read/write operations
 * to execute. The {@link OperationController} created when spec-generation begins is granted
 * access to the private, singleton instance of the internal class {@link HBaseDelegate}, which is
 * responsible for interpreting and executing the spec once the {@link OperationController}
 * indicates that spec-generation is complete.
 * 
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class HBaseControl implements HBaseStart<OpResultSet>, Closeable {
    
    // ||========================================================================================||
    // ||    INNER CLASSES (INSTANCE)                                                            ||
    // ||----------------------------------------------------------------------------------------||

    /**
     * Internal class implementation owned and controlled by an {@link HBaseControl} instance, and
     * responsible for interpreting and executing the HBase operation(s) indicated by an operation
     * specification. HBaseDelegate maintains no internal state of its own, and only references the
     * {@link HBaseContext} object governing the configuration of its controlling
     * {@link HBaseControl}, so it should be safe for concurrent use by multiple threads;
     * accordingly, a single instance of it is maintained within an HBaseControl object, and a
     * reference to it is parceled out to {@link OperationController} whenever an operation starts
     * via {@link HBaseControl#begin()}.
     * 
     * @author Branden Smith; Liaison Technologies, Inc.
     */
    public final class HBaseDelegate {

        /**
         * TODO: javadoc
         * @param colFam
         * @param colQual
         * @return
         */
        private EnumSet<VersioningModel> determineVersioningScheme(final FamilyModel colFam, final QualModel colQual) {
            EnumSet<VersioningModel> versioningScheme;

            versioningScheme = colQual.getVersioning();
            if (versioningScheme == null) {
                versioningScheme = colFam.getVersioning();
            }
            return versioningScheme;
        }

        private EnumSet<VersioningModel> determineVersioningScheme(final ColSpecFrozen colSpec) {
            return determineVersioningScheme(colSpec.getFamily(), colSpec.getColumn());
        }

        /**
         * TODO: javadoc
         * @param fqpSet
         * @param versioningScheme
         * @param colFam
         * @param colQual
         * @param multiVersion
         */
        private void addVersioningDerivedQualifiers(final Set<FamilyQualifierPair> fqpSet, final EnumSet<VersioningModel> versioningScheme, final FamilyModel colFam, final QualModel colQual, final LongValueSpecFrozen multiVersion) {
            final String logMsg;
            // TODO: figure out how to implement this (only needed for reads)
            logMsg = "Read spec specifies a range of version numbers "
                     + multiVersion
                     + "; not yet supported";
            throw new UnsupportedOperationException(logMsg);
        }

        /**
         * TODO: javadoc
         * @param fqpSet
         * @param versioningScheme
         * @param colFam
         * @param colQual
         * @param singleVersion
         */
        private void addVersioningDerivedQualifiers(final Set<FamilyQualifierPair> fqpSet, final EnumSet<VersioningModel> versioningScheme, final FamilyModel colFam, final QualModel colQual, final Long singleVersion) {
            final byte[] qualValueBase;
            QualModel qualForWrite;
            byte[] qualBytes;

            // we're going to be creating new byte arrays here via concatenation anyway, so
            // skip doing the defensive copy
            qualValueBase = colQual.getName().getValue(DefensiveCopyStrategy.NEVER);

            /*
             * For each versioning scheme specified by the model, add a qualifier to the
             * read spec, modified to accommodate the versioning scheme, if necessary.
             * TODO (important): not clear whether the Set is able to provide uniqueness
             *     guarantees with byte[] type; investigate/correct, if not
             * TODO: does this work for multiple overlapping versioning schemes?
             */
            for (VersioningModel verModel : versioningScheme) {
                // create a new qualifier with the version number appended
                qualBytes =
                    HBaseUtil.appendVersionToQual(qualValueBase,
                        singleVersion.longValue(),
                        verModel);
                qualForWrite = QualModel.of(Name.of(qualBytes, DefensiveCopyStrategy.NEVER));
                // create a new family+qualifier pair pairing the existing column family
                // with the newly-created qualifier
                fqpSet.add(FamilyQualifierPair.of(colFam, qualForWrite));
            }
        }

        /**
         * TODO: javadoc
         * @param fqpSet
         * @param colFam
         * @param colQual
         * @return
         */
        private Set<FamilyQualifierPair> prepareHBaseOpQualifierSet(final Set<FamilyQualifierPair> fqpSet, final FamilyModel colFam, final QualModel colQual) {
            /*
             * Intended for the case where the logic to add versioning-derived qualifiers indicates
             * that qualifier versioning is not in use; in that case, adds a single pair using the
             * base family and qualifier.
             */
            if (fqpSet.size() <= 0) {
                fqpSet.add(FamilyQualifierPair.of(colFam, colQual));
            }
            return Collections.unmodifiableSet(fqpSet);
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @param colFam
         * @param colQual
         * @param dcs
         * @return
         */
        private Set<FamilyQualifierPair> getVersionAdjustedQualifiersForWrite(final ColSpecWriteFrozen colSpec, final FamilyModel colFam, final QualModel colQual, final DefensiveCopyStrategy dcs) {
            final Set<FamilyQualifierPair> fqpSet;
            final Long singleVersion;
            EnumSet<VersioningModel> versioningScheme;

            fqpSet = new HashSet<FamilyQualifierPair>();
            singleVersion = colSpec.getVersion();
            if (singleVersion != null) {
                versioningScheme = determineVersioningScheme(colFam, colQual);
                if (VersioningModel.isQualifierBased(versioningScheme)) {
                    addVersioningDerivedQualifiers(fqpSet,
                                                   versioningScheme,
                                                   colFam,
                                                   colQual,
                                                   singleVersion);
                }
            }
            return prepareHBaseOpQualifierSet(fqpSet, colFam, colQual);
        }

        /**
         * TODO
         * @param colSpec
         * @param colFam
         * @param colQual
         * @param dcs
         * @return
         */
        private Set<FamilyQualifierPair> getVersionAdjustedQualifiersForRead(final ColSpecRead<ReadOpSpecDefault> colSpec, final FamilyModel colFam, final QualModel colQual, final DefensiveCopyStrategy dcs) {
            final Set<FamilyQualifierPair> fqpSet;
            final LongValueSpec<?> version;
            final Long singleVersion;
            EnumSet<VersioningModel> versioningScheme;

            fqpSet = new HashSet<>();
            version = colSpec.getVersion();
            if (version != null) {
                versioningScheme = determineVersioningScheme(colFam, colQual);
                if (VersioningModel.isQualifierBased(versioningScheme)) {
                    singleVersion = version.singleValue();
                    if (singleVersion == null) {
                        /*
                         * TODO
                         * Per the API, a null value here means that the version number specified
                         * for reading is a range, not a single version value. That feature is not
                         * currently implemented, so for now throw an UnsupportedOpExc. Determine
                         * how to make this work at a later time.
                         */
                        addVersioningDerivedQualifiers(fqpSet,
                                                       versioningScheme,
                                                       colFam,
                                                       colQual,
                                                       version);
                    } else {
                        addVersioningDerivedQualifiers(fqpSet,
                                                       versioningScheme,
                                                       colFam,
                                                       colQual,
                                                       singleVersion);
                    }
                }
            }
            return prepareHBaseOpQualifierSet(fqpSet, colFam, colQual);
        }

        /**
         * TODO
         * @param logMethodName
         * @param dcs
         * @param readGet
         * @param colSpec
         */
        private void addColumn(final String logMethodName, final DefensiveCopyStrategy dcs, final Get readGet, final ColSpecRead<ReadOpSpecDefault> colSpec) {
            final ReadOpSpecFrozen readOpSpec;
            final FamilyModel colFam;
            final QualModel colQual;
            final byte[] famValue;
            final Set<FamilyQualifierPair> fqpSet;

            if (colSpec != null) {
                readOpSpec = colSpec.getParent();
                colFam = colSpec.getFamily();
                colQual = colSpec.getColumn();
                if (colFam != null) {
                    famValue = colFam.getName().getValue(dcs);
                    if (colQual != null) {
                        // Generate the set of family-qualifier pairs to which the given column
                        // specification refers, adding any version-specific adjustments to the
                        // qualifier values as needed
                        fqpSet =
                            getVersionAdjustedQualifiersForRead(colSpec, colFam, colQual, dcs);
                        // Update the column specification to associate it with the set of
                        // generated family-qualifier pairs being added to the HBase Get
                        colSpec.setResultColumnAssoc(fqpSet);
                        for (FamilyQualifierPair fqp : fqpSet) {
                            /*
                             * TODO
                             * it might be worth investigating modifying
                             * getVersionAdjustedQualifiersForRead such that it guarantees that the
                             * return values are already defensive copies, to avoid the possibility
                             * of duplicate defensive-copying here
                             */
                            readGet.addColumn(fqp.getFamily().getName().getValue(dcs),
                                              fqp.getQual().getName().getValue(dcs));

                            // Update the parent read operation spec to associate it with this
                            // family-qualifier pair
                            readOpSpec.addColumnAssoc(fqp, colSpec);

                            LOG.trace(logMethodName,
                                      ()->"adding to GET: family=",
                                      fqp::getFamily,
                                      ()->", qual=",
                                      fqp::getQual);
                        }
                    } else {
                        /*
                         * TODO: versioning on qualifier when reading from a full family?
                         */
                        readGet.addFamily(famValue);

                        // Update the parent read operation spec to associate it with this
                        // column family reference
                        readOpSpec.addColumnAssoc(colFam, colSpec);

                        LOG.trace(logMethodName,
                                  ()->"adding to GET: family=",
                                  ()->colFam);
                    }
                }
            }
        }

        /**
         * TODO: javadoc
         * @param colSpec
         * @return
         */
        private Long determineWriteTimestamp(final ColSpecWriteFrozen colSpec) {
            final Long version;

            version = colSpec.getVersion();
            if ((version != null)
                && (VersioningModel.isTimestampBased(determineVersioningScheme(colSpec)))) {
                /*
                 * (NOTE: this list of TODOs copied from setReadTimestamp, because the issues they
                 * reference will need to addressed concurrently on both the read and write sides)
                 *
                 * TODO: Determine how to support multiple overlapping versioning models; that
                 *     feature is not supported here yet, at all
                 * TODO: Determine how to support TIMESTAMP_CHRONO, which would need to subtract
                 *     the version numbers from Long.MAX_VALUE and invert the range. Not
                 *     implemented yet.
                 * TODO: should probably throw some kind of validation exception earlier if both
                 *     timestamp and version information are specified, and the versioning conflict
                 *     specifies to use the timestamp... that is never a valid configuration, as
                 *     the literally-specified timestamp will always be overridden by the
                 *     versioning-specified timestamp
                 */
                return version;
            } else {
                return colSpec.getTS();
            }
        }

        /**
         * TODO
         * @param logMethodName
         * @param dcs
         * @param writePut
         * @param colSpec
         */
        private void addColumn(final String logMethodName, final DefensiveCopyStrategy dcs, final Put writePut, final ColSpecWriteFrozen colSpec) {
            final Long writeTS;
            final FamilyModel colFam;
            final QualModel colQual;
            final NullableValue colValue;
            final Set<FamilyQualifierPair> fqpSet;
            
            writeTS = determineWriteTimestamp(colSpec);
            colFam = colSpec.getFamily();
            colQual = colSpec.getColumn();
            colValue = colSpec.getValue();

            fqpSet = getVersionAdjustedQualifiersForWrite(colSpec, colFam, colQual, dcs);
            for (FamilyQualifierPair fqp: fqpSet) {
                if (writeTS == null) {
                    writePut.add(fqp.getFamily().getName().getValue(dcs),
                                 fqp.getQual().getName().getValue(dcs),
                                 colValue.getValue(dcs));
                    LOG.trace(logMethodName,
                              () -> "adding to PUT: family=",
                              fqp::getFamily,
                              () -> ", qual=",
                              fqp::getQual,
                              () -> ", value='",
                              () -> colValue,
                              () -> "'");
                } else {
                    writePut.add(colFam.getName().getValue(dcs),
                                 colQual.getName().getValue(dcs),
                                 writeTS.longValue(),
                                 colValue.getValue(dcs));
                    LOG.trace(logMethodName,
                              () -> "adding to PUT: family=",
                              fqp::getFamily,
                              () -> ", qual=",
                              fqp::getQual,
                              () -> ", value='",
                              () -> colValue,
                              () -> "', ts=",
                              () -> writeTS);
                }
            }
        }
        
        /**
         * TODO
         * @param logMethodName
         * @param writeToTable
         * @param tableRowSpec
         * @param colWriteList
         * @param condition
         * @param writePut
         * @param dcs
         * @return
         * @throws HBaseMultiColumnException
         */
        private boolean performWrite(final String logMethodName, final HTable writeToTable, final RowSpec<WriteOpSpecDefault> tableRowSpec, final List<ColSpecWriteFrozen> colWriteList, final CondSpec<?> condition, final Put writePut, final DefensiveCopyStrategy dcs) throws HBaseMultiColumnException {
            final String logMsg;
            final NullableValue condPossibleValue;
            final RowKey rowKey;
            final FamilyModel fam;
            final QualModel qual;
            final boolean writeCompleted;
            
            try {
                if (condition != null) {
                    LOG.trace(logMethodName,
                              ()->"on-condition: ",
                              ()->condition);
                    condPossibleValue = condition.getValue();
                    rowKey = condition.getRowKey();
                    fam = condition.getFamily();
                    qual = condition.getColumn();
                    
                    LOG.trace(logMethodName, ()->"performing write...");
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
                    LOG.trace(logMethodName, ()->"performing write...");
                    writeToTable.put(writePut);
                    writeCompleted = true;
                }
                LOG.trace(logMethodName,
                          ()->"write operation response: ",
                          ()->Boolean.toString(writeCompleted));
            } catch (IOException ioExc) {
                logMsg = ("WRITE failure"
                          + ((condition == null)
                             ?"; "
                             :" (with condition: " + condition + "); ")
                          + ioExc);
                LOG.error(logMethodName, logMsg, ioExc);
                throw new HBaseMultiColumnException(tableRowSpec, colWriteList, logMsg, ioExc);
            }
            return writeCompleted;
        }

        /**
         * TODO
         * @param logMethodName
         * @param readGet
         * @param readSpec
         * @param tableRowSpec
         * @throws HBaseTableRowException
         */
        private void setReadTimestamp(final String logMethodName, final Get readGet, final ReadOpSpecDefault readSpec, final RowSpec<?> tableRowSpec) throws HBaseTableRowException {
            String logMsg;
            final LongValueSpecFrozen commonVer;
            final EnumSet<VersioningModel> commonVerConf;
            final LongValueSpecFrozen timestamp;

            commonVer = readSpec.getCommonVersion();
            commonVerConf = readSpec.getCommonVersioningConfig();

            if ((commonVer != null)
                && (commonVerConf != null)
                && (VersioningModel.isTimestampBased(commonVerConf))) {
                /*
                 * TODO: Determine how to support multiple overlapping versioning models; that
                 *     feature is not supported here yet, at all
                 * TODO: Determine how to support TIMESTAMP_CHRONO, which would need to subtract
                 *     the version numbers from Long.MAX_VALUE and invert the range. Not
                 *     implemented yet.
                 * TODO: should probably throw some kind of validation exception earlier if both
                 *     timestamp and version information are specified, and the versioning conflict
                 *     specifies to use the timestamp... that is never a valid configuration, as
                 *     the literally-specified timestamp will always be overridden by the
                 *     versioning-specified timestamp
                 */
                if (commonVerConf.contains(VersioningModel.TIMESTAMP_CHRONO)) {
                    logMsg = "Chronological versioning via the timestamp ("
                             + VersioningModel.class.getSimpleName()
                             + "."
                             + VersioningModel.TIMESTAMP_CHRONO
                             + " is not yet implemented";
                    throw new UnsupportedOperationException(logMsg);
                }
                // TODO: this code assumes TIMESTAMP_LATEST is the only versioning scheme
                timestamp = commonVer;
            } else {
                timestamp = readSpec.getAtTime();
            }
            try {
                ReadUtils.applyTS(readGet, timestamp);
                LOG.trace(logMethodName,
                          ()->"applied timestamp/version constraints (if applicable): ts=",
                          ()->readSpec.getAtTime(),
                          ()->",common-version=",
                          ()->commonVer,
                          ()->",common-versioning-config=",
                          ()->readSpec.getCommonVersioningConfig());
            } catch (IOException ioExc) {
                logMsg = "Failed to apply timestamp cond to READ per spec: "
                         + readSpec + "; " + ioExc;
                LOG.error(logMethodName, logMsg, ioExc);
                throw new HBaseTableRowException(tableRowSpec, logMsg, ioExc);
            }
        }

        /**
         * TODO
         * @param readSpec
         * @return
         * @throws IllegalArgumentException
         * @throws HBaseException
         * @throws HBaseRuntimeException
         */
        public Result exec(final ReadOpSpecDefault readSpec) throws IllegalArgumentException, HBaseException, HBaseRuntimeException {
            String logMsg;
            final String logMethodName;
            final DefensiveCopyStrategy dcs;
            final RowSpec<?> tableRowSpec;
            final Integer maxResultsPerFamily;
            final Get readGet;
            final List<ColSpecRead<ReadOpSpecDefault>> colReadList;
            final Result res;
            
            Util.ensureNotNull(readSpec, this, "readSpec", ReadOpSpecDefault.class);
            
            logMethodName =
                LOG.enter(()->"exec(READ:",
                          ()->String.valueOf(readSpec.getHandle()),
                          ()->")");
            
            // Ensure that the spec contains all required attributes for a READ operation
            verifyStateForExec(readSpec);
            
            dcs = HBaseControl.this.context.getDefensiveCopyStrategy();
            LOG.trace(logMethodName,
                      ()->"defensive-copying: ",
                      ()->String.valueOf(dcs));
            
            tableRowSpec = readSpec.getTableRow();
            LOG.trace(logMethodName,
                      ()->"table-row: ",
                      ()->tableRowSpec);
            
            try (ManagedTable readFromTable =
                    resMgr.borrow(HBaseControl.this.context, tableRowSpec.getTable())) {
                
                LOG.trace(logMethodName, ()->"table obtained");
                
                readGet = new Get(tableRowSpec.getRowKey().getValue(dcs));

                maxResultsPerFamily = readSpec.getMaxEntriesPerFamily();
                if (maxResultsPerFamily != null) {
                    readGet.setMaxResultsPerColumnFamily(maxResultsPerFamily.intValue());
                    LOG.trace(logMethodName,
                              ()->"applied maximum number of columns to read per family: ",
                              ()->maxResultsPerFamily);
                }

                setReadTimestamp(logMethodName, readGet, readSpec, tableRowSpec);
                
                colReadList = readSpec.getWithColumn();
                LOG.trace(logMethodName,
                          ()->"columns: ",
                          ()->colReadList);
                if (colReadList != null) {
                    for (ColSpecRead<ReadOpSpecDefault> colSpec : colReadList) {
                        addColumn(logMethodName, dcs, readGet, colSpec);
                    }
                }

                LOG.trace(logMethodName, ()->"performing read...");
                try {
                    res = readFromTable.use().get(readGet);
                } catch (IOException ioExc) {
                    logMsg = "READ failed; " + ioExc;
                    LOG.error(logMethodName, logMsg, ioExc);
                    throw new HBaseMultiColumnException(tableRowSpec, colReadList, logMsg, ioExc);
                }
                
                LOG.trace(logMethodName,
                          ()->"read complete; result: ",
                          ()->res);
                if ((res == null) || (res.isEmpty())) {
                    logMsg = "READ failed; null/empty result set";
                    LOG.error(logMethodName, logMsg);
                    throw new HBaseEmptyResultSetException(tableRowSpec, colReadList, logMsg);
                }
            } catch (HBaseException | HBaseRuntimeException exc) {
                // already logged; just rethrow to get out of the current try block
                throw exc;
            } catch (Exception exc) {
                logMsg = "Unexpected failure during READ operation ("
                         + readSpec
                         + "): "
                         + exc.toString();
                LOG.error(logMsg, logMethodName, exc);
                throw new HBaseRuntimeException(logMsg, exc);
            } finally {
                LOG.leave(logMethodName);
            }
            return res;
        }
        
        /**
         * 
         * @param writeSpec
         * @return
         * @throws IllegalArgumentException
         * @throws IllegalStateException
         * @throws HBaseException
         * @throws HBaseRuntimeException
         */
        public boolean exec(final WriteOpSpecDefault writeSpec) throws IllegalArgumentException, IllegalStateException, HBaseException, HBaseRuntimeException {
            String logMsg;
            final String logMethodName;
            final DefensiveCopyStrategy dcs;
            final RowSpec<WriteOpSpecDefault> tableRowSpec;
            final List<ColSpecWriteFrozen> colWriteList;
            final CondSpec<?> condition;
            final Put writePut;
            boolean writeCompleted;
            
            Util.ensureNotNull(writeSpec, this, "writeSpec", WriteOpSpecDefault.class);
            
            logMethodName =
                LOG.enter(()->"exec(WRITE:",
                          ()->String.valueOf(writeSpec.getHandle()),
                          ()->")");
            writeCompleted = false;
            
            // Ensure that the spec contains all required attributes for a WRITE operation
            verifyStateForExec(writeSpec);
            
            dcs = HBaseControl.this.context.getDefensiveCopyStrategy();
            LOG.trace(logMethodName,
                      ()->"defensive-copying: ",
                      ()->String.valueOf(dcs));
            
            tableRowSpec = writeSpec.getTableRow();
            LOG.trace(logMethodName,
                    ()->"table-row: ",
                    ()->tableRowSpec);

            try (ManagedTable writeToTable =
                    resMgr.borrow(HBaseControl.this.context, tableRowSpec.getTable())) {
                LOG.trace(logMethodName, ()->"table obtained");
                
                writePut = new Put(tableRowSpec.getRowKey().getValue(dcs));
                
                colWriteList = writeSpec.getWithColumn();
                LOG.trace(logMethodName,
                          ()->"columns: ",
                          ()->colWriteList);
                if (colWriteList != null) {
                    for (ColSpecWriteFrozen colWrite : colWriteList) {
                        addColumn(logMethodName, dcs, writePut, colWrite);
                    }
                }
                
                condition = writeSpec.getGivenCondition();
                writeCompleted =
                    this.performWrite(logMethodName,
                                      writeToTable.use(),
                                      tableRowSpec,
                                      colWriteList,
                                      condition,
                                      writePut,
                                      dcs);
            } catch (HBaseException | HBaseRuntimeException exc) {
                throw exc;
            } catch (Exception exc) {
                logMsg = "Unexpected failure during WRITE operation ("
                         + writeSpec
                         + "): "
                         + exc.toString();
                LOG.error(logMethodName, logMsg, exc);
                throw new HBaseException(logMsg, exc);
            }
            return writeCompleted;
        }
        
        /**
         * Tunnelling method so that OperationController with access to the delegate can use the
         * execution thread pool established in the HBaseControl.
         * @param operationExecutable
         * @return
         * @throws UnsupportedOperationException
         */
        public ListenableFuture<OpResultSet> execAsync(Callable<OpResultSet> operationExecutable) throws UnsupportedOperationException {
            String logMsg;
            final ListeningExecutorService asyncPool;
            final ListenableFuture<OpResultSet> execTask;
            asyncPool = HBaseControl.this.execPool;
            if (asyncPool == null) {
                logMsg = HBaseControl.class.getSimpleName()
                         + " (context.id='"
                         + HBaseControl.this.context.getId()
                         + "') does not support asynchronous operations";
                throw new UnsupportedOperationException(logMsg);
            }
            execTask = asyncPool.submit(operationExecutable);
            return execTask;
        }
        
        /**
         * Use a private constructor so that the enclosing HBaseControl instance can control who
         * has access to the delegate (and, consequently, who can execute HBase operations based
         * upon specifications).
         */
        private HBaseDelegate() { }
    }
    
    // ||----(inner classes: instance)-----------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    CONSTANTS                                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    private static final long DEFAULT_THREADPOOL_IDLEEXPIRE = 60L * 1000L; // 60s
    private static final TimeUnit DEFAULT_THREADPOOL_IDLEEXPIRE_UNIT = TimeUnit.MILLISECONDS;
    
    private static final LogMeMaybe LOG;
    
    // ||----(constants)-------------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    STATIC METHODS                                                                      ||
    // ||----------------------------------------------------------------------------------------||
    
    private static void verifyStateForExec(final OperationSpec<?> opSpec) throws IllegalStateException {
        final String logMethodName;
        
        logMethodName =
            LOG.enter(()->"verifyStateForExec(spec:",
                      ()->opSpec,
                      ()->")");
        if (!opSpec.isFrozen()) {
            throw new IllegalStateException(opSpec.getClass().getSimpleName()
                                            + " must be frozen before spec may be executed"); 
        }
        LOG.leave(logMethodName);
    }
    
    // ||----(static methods)--------------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    STATIC INITIALIZER                                                                  ||
    // ||----------------------------------------------------------------------------------------||
    
    static {
        LOG = new LogMeMaybe(HBaseControl.class);
    }
    
    // ||----(static initializer)----------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private final HBaseContext context;
    private final HBaseResourceManager resMgr;
    private final HBaseDelegate delegate;
    private final ListeningExecutorService execPool;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS                                                                    ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * Shut down the asynchronous execution pool, if one was created.
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
        if (this.execPool != null) {
            this.execPool.shutdown();
        }
    }
    
    /**
     * 
     * @return
     */
    public HBaseContext getContext() {
        return this.context;
    }
    
    /**
     * {@inheritDoc}
     * @see {@link HBaseStart#begin()}.
     */
    public OperationController<OpResultSet> begin() {
        return new OperationControllerDefault(this.delegate, this.context);
    }
    
    // ||----(instance methods)------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||
    
    public HBaseControl(final HBaseContext context, final HBaseResourceManager resMgr) {
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
        Util.ensureNotNull(resMgr, this, "resMgr", HBaseResourceManager.class);
        this.resMgr = resMgr;
        this.delegate = new HBaseDelegate();
        if (context.getAsyncConfig().isAsyncEnabled()) {
            this.execPool =
                MoreExecutors.listeningDecorator(
                    new ThreadPoolExecutor(context.getAsyncConfig().getMinSizeForThreadPool(),
                                           context.getAsyncConfig().getMaxSizeForThreadPool(),
                                           DEFAULT_THREADPOOL_IDLEEXPIRE,
                                           DEFAULT_THREADPOOL_IDLEEXPIRE_UNIT,
                                           new SynchronousQueue<Runnable>()));
        } else {
            this.execPool = null;
        }
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
