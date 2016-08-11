package com.liaison.shachi.test.e2e;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.logging.JitLog;
import com.liaison.javabasics.serialization.BytesUtil;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.HBaseStart;
import com.liaison.shachi.api.request.OperationController;
import com.liaison.shachi.api.response.OpResultSet;
import com.liaison.shachi.context.DirectoryPrefixedTableNamingStrategy;
import com.liaison.shachi.context.MapRHBaseContext;
import com.liaison.shachi.context.TableNamingStrategy;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.model.VersioningModel;
import com.liaison.shachi.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.18 14:23
 */
public class DatasetSimulation {

    private static final JitLog LOG;

    private static final String SYSPROP_PATH_MAPRTABLES = "PATH_MAPRTABLES";

    private static final long ACTIONINDEX_INIT = 0;

    private static final String HBREAD_START_ACTIONID = "get-action-start-id";
    private static final String HBREADFIELD_ACTIONSTART = "action-start";
    private static final String HBWRITE_START_ACTIONID_INCR = "increment-action-start-id";
    private static final String HBWRITE_META = "write-all-the-meta";
    private static final String HBWRITE_DATA = "write-all-the-data";
    private static final String HBWRITE_DATAMETA = "write-update-specific-meta";
    private static final String HBWRITE_COMPLETE_ACTIONID = "put-action-complete-id";

    private static final String USERINPUT_OP_CREATE = "C";
    private static final String USERINPUT_OP_REPLACE = "R";
    private static final String USERINPUT_OP_UPDATE = "U";
    private static final String USERINPUT_OP_DELETE = "D";

    private static final String USERINPUT_YES = "Y";

    private static final QualModel QUAL_DATA_COLUMN =
        QualModel
            .with(Name.of("a"))
            .versionWith(VersioningModel.QUALIFIER_LATEST)
            .build();
    private static final QualModel QUAL_ACTION_START =
        QualModel
            .with(Name.of("("))
            .build();
    private static final QualModel QUAL_ACTION_COMPLETE =
        QualModel
            .with(Name.of(")"))
            .build();
    private static final FamilyModel FAMILY_DATA =
        FamilyModel
            .with(Name.of("d"))
            .qual(QUAL_DATA_COLUMN)
            .build();
    private static final FamilyModel FAMILY_META =
        FamilyModel
            .with(Name.of("m"))
            .qual(QUAL_ACTION_START)
            .qual(QUAL_ACTION_COMPLETE)
            .build();
    private static final FamilyModel FAMILY_DATAMETA =
        FamilyModel
            .with(Name.of("u"))
            /*
            uses the same qualifier model as FAMILY_DATA, because these updates will be parallel to the data puts
             */
            .qual(QUAL_DATA_COLUMN)
            .build();
    private static final TableModel TABLE_PRIMEDATA =
        TableModel
            .with(Name.of("PRIME"))
            .family(FAMILY_DATA)
            .family(FAMILY_META)
            .family(FAMILY_DATAMETA)
            .build();

    private static HBaseControl createControl(final TableNamingStrategy namingStrategy) {
        final HBaseControl hbc;

        if (namingStrategy == null) {
            hbc =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .id(DatasetSimulation.class.getSimpleName())
                        .configProvider(HBaseConfiguration::create)
                        .build(),
                    SimpleHBaseResourceManager.INSTANCE);
        } else {
            hbc =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .id(DatasetSimulation.class.getSimpleName())
                        .configProvider(HBaseConfiguration::create)
                        .tableNamingStrategy(namingStrategy)
                        .build(),
                    SimpleHBaseResourceManager.INSTANCE);
        }
        return hbc;
    }
    private static HBaseControl createControl() {
        return createControl(null);
    }

    static {
        LOG = new JitLog(DatasetSimulation.class);
    }

    private final HBaseStart<OpResultSet> ctrl;

    private Long readActionStart(final RowKey rowKey) throws HBaseException {
        OpResultSet result;

        result =
            ctrl
                .begin()
                    .read(HBREAD_START_ACTIONID)
                        .from()
                            .tbl(TABLE_PRIMEDATA)
                            .row(rowKey)
                            .and()
                        .with(HBREADFIELD_ACTIONSTART)
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .and()
                        .atMost(1)
                        .then()
                    .exec();
        return
            BytesUtil.toLong(
                result
                    .getReadResult(HBREAD_START_ACTIONID)
                    .getData(HBREADFIELD_ACTIONSTART)
                    .get(0)
                    .getDatum()
                    .getValue(DefensiveCopyStrategy.NEVER));
    }

    private long initializeActionStart(final RowKey rowKey) throws HBaseException {
        final Long currentVersion;
        final Long nextVersion;
        boolean writeCompleted = false;

        currentVersion = Long.valueOf(ACTIONINDEX_INIT);
        nextVersion = Long.valueOf(ACTIONINDEX_INIT + 1);
        writeCompleted =
            ctrl
                .begin()
                    .write(HBWRITE_START_ACTIONID_INCR)
                        .on()
                            .tbl(TABLE_PRIMEDATA)
                            .row(rowKey)
                            .and()
                        .with()
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .value(Value.of(BytesUtil.toBytes(nextVersion),
                                DefensiveCopyStrategy.NEVER))
                            .and()
                        .given()
                            .row(rowKey)
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .empty()
                            .and()
                        .then()
                    .exec()
                .getWriteResult(HBWRITE_START_ACTIONID_INCR)
                .isMutationPerformed();
        if (!writeCompleted) {
            throw new HBaseException("Failed to initialize column version: "
                                     + QUAL_ACTION_START);
        }
        return currentVersion;
    }

    private long readIncrementActionStart(final RowKey rowKey) throws HBaseException {
        Long currentVersion = null;
        long nextVersion;
        boolean writeCompleted = false;

        try {
            currentVersion = readActionStart(rowKey);
        } catch (HBaseException exc) {
            LOG.error("----- EXCEPTION ----- " + exc, exc);
            currentVersion = Long.valueOf(ACTIONINDEX_INIT);
        }
        nextVersion = currentVersion.longValue() + 1;
        writeCompleted =
            ctrl
                .begin()
                    .write(HBWRITE_START_ACTIONID_INCR)
                        .on()
                            .tbl(TABLE_PRIMEDATA)
                            .row(rowKey)
                            .and()
                        .with()
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .value(Value.of(BytesUtil.toBytes(nextVersion),
                                DefensiveCopyStrategy.NEVER))
                            .and()
                /*
                        .given()
                            .row(rowKey)
                            .fam(FAMILY_META)
                            .column(QUAL_ACTION_START);
                            .value(Value.of(BytesUtil.toBytes(currentVersion.longValue()),
                                DefensiveCopyStrategy.NEVER))
                            .and()
                */
                        .then()
                    .exec()
                .getWriteResult(HBWRITE_START_ACTIONID_INCR)
                .isMutationPerformed();
        if (!writeCompleted) {
            throw new HBaseException("Failed to increment column version: "
                                     + QUAL_ACTION_START);
        }
        return currentVersion.longValue();
    }

    private void writeActionComplete(final RowKey rowKey, final long actionId) throws HBaseException {
        boolean writeCompleted;

        writeCompleted =
            ctrl
                .begin()
                    .write(HBWRITE_COMPLETE_ACTIONID)
                        .on()
                            .tbl(TABLE_PRIMEDATA)
                            .row(rowKey)
                            .and()
                        .with()
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_COMPLETE)
                            .value(Value.of(BytesUtil.toBytes(actionId),
                                            DefensiveCopyStrategy.NEVER))
                            .ts(actionId)
                            .and()
                        .then()
                    .exec()
                .getWriteResult(HBWRITE_COMPLETE_ACTIONID)
                .isMutationPerformed();
        if (!writeCompleted) {
            throw new HBaseException("Failed to update column version: "
                                     + QUAL_ACTION_COMPLETE);
        }
    }

    private void writeData(final RowKey rowKey, final String data, final Map<String, String> meta, final String dataMeta, final long actionId) throws HBaseException {
        final OpResultSet result;
        final OperationController<OpResultSet> hbTrans;

        hbTrans = ctrl.begin();
        if ((meta != null) && (!meta.isEmpty())) {
            /*
             * TODO: implement onCondition() on the OperationSpec to allow the spec to be enabled
             * or disabled based on the result of a Predicate (lambda) evaluated at validate()
             * time, so that breaking the flow of the fluent API like this is not necessary.
             */
            hbTrans
                .write(HBWRITE_META)
                    .on()
                        .tbl(TABLE_PRIMEDATA)
                        .row(rowKey)
                        .and()
                    .withAllOf(meta.entrySet(), (entry, colSpec) -> {
                        final QualModel metaColQual;
                        metaColQual =
                            QualModel
                                .with(Name.of(entry.getKey()))
                                .versionWith(VersioningModel.TIMESTAMP_LATEST)
                                .build();
                        colSpec
                            .fam(FAMILY_META)
                            .qual(metaColQual)
                            .value(Value.of(entry.getValue()))
                            .version(actionId)
                            .ts(actionId); // not really necessary, since using ts for version
                                           // here, will get overridden by version logic
                    })
                    .done();
        }
        result =
            hbTrans
                .write(HBWRITE_DATAMETA)
                    .on()
                        .tbl(TABLE_PRIMEDATA)
                        .row(rowKey)
                        .and()
                    .with()
                        .fam(FAMILY_DATAMETA)
                        .qual(QUAL_DATA_COLUMN)
                        .value(Value.of(dataMeta))
                        .version(actionId)
                        .ts(actionId)
                        .and()
                    .then()
                .write(HBWRITE_DATA)
                    .on()
                        .tbl(TABLE_PRIMEDATA)
                        .row(rowKey)
                        .and()
                    .with()
                        .fam(FAMILY_DATA)
                        .qual(QUAL_DATA_COLUMN)
                        .value(Value.of(data))
                        .version(actionId)
                        .ts(actionId)
                        .and()
                    .then()
                .exec();
        if ((meta != null)
            && (!meta.isEmpty())
            && (!result.getWriteResult(HBWRITE_META).isMutationPerformed())) {
            throw new HBaseException("Failed to write meta-data");
        }
        if (!result.getWriteResult(HBWRITE_DATA).isMutationPerformed()) {
            throw new HBaseException("Failed to write data and/or meta-data");
        }
    }

    public void persist(final String rowKeyStr, final String data, final Map<String, String> meta, final String dataMeta, final boolean createNew) throws HBaseException {
        final RowKey rowKey;
        final long actionId;

        rowKey = RowKey.of(rowKeyStr);
        if (createNew) {
            actionId = initializeActionStart(rowKey);
        } else {
            actionId = readIncrementActionStart(rowKey);
        }
        writeData(rowKey, data, meta, dataMeta, actionId);
        writeActionComplete(rowKey, actionId);
    }

    public DatasetSimulation() {
        final String tablesPathPrefix;

        tablesPathPrefix = Util.simplify(System.getProperty(SYSPROP_PATH_MAPRTABLES));
        if (tablesPathPrefix != null) {
            LOG.trace("Creating HBase control using directory naming prefix: " + tablesPathPrefix);
            this.ctrl = createControl(new DirectoryPrefixedTableNamingStrategy(tablesPathPrefix));
        } else {
            this.ctrl = createControl();
        }
    }

    private static String next(final Scanner inScan) {
        return Util.simplify(inScan.nextLine());
    }

    public static void main(String[] arguments) {
        final DatasetSimulation dataSim;
        Map<String, String> meta;
        String metaKey;
        String metaValue;
        String data;
        String dataMeta;
        String rowKeyStr;
        String opStr;

        meta = new HashMap<>();
        dataSim = new DatasetSimulation();
        try (final Scanner inScan = new Scanner(System.in)) {
            do {
                metaKey = null;
                metaValue = null;
                data = null;
                dataMeta = null;
                rowKeyStr = null;
                meta.clear();

                System.out.print("[C]reate | [R]eplace | [U]pdate | [D]elete: ");
                opStr = next(inScan);
                if (opStr != null) {
                    opStr = opStr.substring(0, 1).toUpperCase();
                }

                // input row key
                System.out.print("ROWKEY: ");
                rowKeyStr = next(inScan);
                if (rowKeyStr != null) {

                    // input metadata map (optional)
                    System.out.print("META? [y/n] ");
                    if (USERINPUT_YES.equalsIgnoreCase(next(inScan))) {
                        do {
                            System.out.print("    META-key: ");
                            metaKey = next(inScan);
                            System.out.print("    META-value: ");
                            metaValue = next(inScan);
                            if ((metaKey != null) && (metaValue != null)) {
                                meta.put(metaKey, metaValue);
                            }
                        } while ((metaKey != null) && (metaValue != null));
                    }

                    // input update-specific metadata blob
                    System.out.print("DATA-META: ");
                    dataMeta = next(inScan);
                    if (dataMeta != null) {

                        // input data
                        System.out.print("DATA: ");
                        data = next(inScan);
                        if (data != null) {
                            dataSim.persist(rowKeyStr, data, meta, dataMeta, USERINPUT_OP_CREATE.equals(opStr));
                        }
                    }
                }
            } while ((rowKeyStr != null) && (dataMeta != null) && (data != null));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
