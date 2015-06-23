package com.liaison.hbase.test.e2e;

import com.liaison.commons.BytesUtil;
import com.liaison.commons.DefensiveCopyStrategy;
import com.liaison.commons.Util;
import com.liaison.commons.log.LogMeMaybe;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.HBaseStart;
import com.liaison.hbase.api.request.OperationController;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.MapRHBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.model.*;
import com.liaison.hbase.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.18 14:23
 */
public class DatasetSimulation {

    private static final LogMeMaybe LOG;

    private static final long ACTIONINDEX_INIT = 0;

    private static final String HBREAD_START_ACTIONID = "get-action-start-id";
    private static final String HBREADFIELD_ACTIONSTART = "action-start";
    private static final String HBWRITE_START_ACTIONID_INCR = "increment-action-start-id";
    private static final String HBWRITE_META = "write-all-the-meta";
    private static final String HBWRITE_DATA = "write-all-the-data";
    private static final String HBWRITE_COMPLETE_ACTIONID = "put-action-complete-id";

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
    private static final TableModel TABLE_PRIMEDATA =
        TableModel
            .with(Name.of("PRIME"))
            .family(FAMILY_DATA)
            .family(FAMILY_META)
            .build();

    private static HBaseControl createControl() {
        final HBaseControl hbc;

        hbc =
            new HBaseControl(
                MapRHBaseContext
                    .getBuilder()
                    .id(DatasetSimulation.class.getSimpleName())
                    .configProvider(HBaseConfiguration::create)
                    .build(),
                SimpleHBaseResourceManager.INSTANCE);
        return hbc;
    }

    static {
        LOG = new LogMeMaybe(DatasetSimulation.class);
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
        try {
            return
                BytesUtil.toLong(
                    result
                        .getReadResult(HBREAD_START_ACTIONID)
                        .getData(HBREADFIELD_ACTIONSTART)
                        .get(0)
                        .getValue(DefensiveCopyStrategy.NEVER));
        } catch (IndexOutOfBoundsException exc) {
            /*
             * TODO: this is a temporary fix until the framework is updated to properly throw an
             * exception when a required field fails to retrieve an element; not sure why it isn't
             * working now.
             */
            throw new HBaseException("No value for " + QUAL_ACTION_START);
        }
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
                            .qual(QUAL_ACTION_START);
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

    private void writeData(final RowKey rowKey, final String data, final Map<String, String> meta, final long actionId) throws HBaseException {
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

    public void persist(final String rowKeyStr, final String data, final Map<String, String> meta) throws HBaseException {
        final RowKey rowKey;
        final long actionId;

        rowKey = RowKey.of(rowKeyStr);
        actionId = readIncrementActionStart(rowKey);
        writeData(rowKey, data, meta, actionId);
        writeActionComplete(rowKey, actionId);
    }

    public DatasetSimulation() {
        this.ctrl = createControl();
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
        String rowKeyStr;

        meta = new HashMap<>();
        dataSim = new DatasetSimulation();
        try (final Scanner inScan = new Scanner(System.in)) {
            do {
                meta.clear();
                System.out.print("ROWKEY: ");
                rowKeyStr = next(inScan);
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
                System.out.print("DATA: ");
                data = next(inScan);
                if (data != null) {
                    dataSim.persist(rowKeyStr, data, meta);
                }
            } while (data != null);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
