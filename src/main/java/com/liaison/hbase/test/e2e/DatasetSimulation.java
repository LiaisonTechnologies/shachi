package com.liaison.hbase.test.e2e;

import com.liaison.commons.BytesUtil;
import com.liaison.commons.DefensiveCopyStrategy;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.HBaseStart;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.MapRHBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.exception.HBaseNoCellException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.model.VersioningModel;
import com.liaison.hbase.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.06.18 14:23
 */
public class DatasetSimulation {

    private static final long ACTIONINDEX_INIT = 0;

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

    private static final HBaseControl createControl() {
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

    private final HBaseStart<OpResultSet> ctrl;

    private Long readActionStart(final RowKey rowKey) throws HBaseException {
        OpResultSet result;

        result =
            ctrl
                .begin()
                    .read("get-action-start-id")
                        .from()
                            .tbl(TABLE_PRIMEDATA)
                            .row(rowKey)
                            .and()
                        .with("action-start")
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .and()
                        .atMost(1)
                        .then()
                    .exec();
        return
            BytesUtil.toLong(
                result
                    .getReadResult("get-action-start-id")
                    .getData("action-start")
                    .get(0)
                    .getValue(DefensiveCopyStrategy.NEVER));
    }

    private long getActionStart(final String rowKeyStr) throws HBaseException {
        final RowKey rowKey;

        rowKey = RowKey.of(rowKeyStr);
        try {
            return readActionStart(rowKey).longValue();
        } catch (HBaseNoCellException exc) {
            ctrl
                .begin()
                    .write("write-init-action-start-id")
                        .on()
                            .tbl(TABLE_PRIMEDATA)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with()
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .value(Value.of(BytesUtil.toBytes(ACTIONINDEX_INIT),
                                            DefensiveCopyStrategy.NEVER))
                            .and()
                        .given()
                            .row(rowKey)
                            .fam(FAMILY_META)
                            .qual(QUAL_ACTION_START)
                            .empty()
                            .and()
                        .then()
                    .exec();
            return readActionStart(rowKey).longValue();
        }
    }

    public DatasetSimulation() {
        this.ctrl = createControl();
    }

    public static void main(String[] arguments) {
        final DatasetSimulation dataSim;


    }
}
