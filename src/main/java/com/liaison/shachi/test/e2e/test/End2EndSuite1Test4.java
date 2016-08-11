package com.liaison.shachi.test.e2e.test;

import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.response.OpResultSet;
import com.liaison.shachi.dto.CellDatum;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.model.TableModel;
import com.liaison.shachi.model.VersioningModel;
import com.liaison.shachi.test.e2e.tools.Verify;

import java.util.UUID;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:01
 */
public class End2EndSuite1Test4 implements End2EndTest {

    private static final JitLog LOG;
    static {
        LOG = new JitLog(End2EndSuite1Test4.class);
    }

    @Override
    public void runTest(final Verify verifier, final HBaseControl ctrl) throws HBaseException {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        String tableName;
        FamilyModel fam;
        QualModel qual;
        TableModel tbl;

        testPrefix = "test4";

        LOG.info(testPrefix, "starting...");

        randomData = UUID.randomUUID().toString();
        tableName = End2EndSuite1Test4.class.getSimpleName() + "-" + randomData;
        rowKeyStr = Long.toString(System.currentTimeMillis());
        fam = FamilyModel.of(Name.of("A"));
        qual =
            QualModel
                .with(Name.of("B"))
                .versionWith(VersioningModel.QUALIFIER_LATEST)
                .build();
        tbl = TableModel.with(Name.of(tableName)).family(fam).build();

        LOG.info(testPrefix, "starting write...");
        opResSet =
            ctrl
                .begin()
                    .write("WRITE")
                        .on()
                            .tbl(tbl)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with("ATVER1")
                            .fam(fam)
                            .qual(qual)
                            .version(1)
                            .value(Value.of(randomData))
                            .and()
                        .with("ATVER2")
                            .fam(fam)
                            .qual(qual)
                            .version(2)
                            .value(Value.of(randomData))
                            .and()
                        .with("ATVER3")
                            .fam(fam)
                            .qual(qual)
                            .version(3)
                            .value(Value.of(randomData))
                            .and()
                        .given()
                            .row(RowKey.of(rowKeyStr))
                            .fam(fam)
                            .qual(QualModel.of(Name.of("NON-EXISTENT")))
                            .empty()
                            .and()
                        .then()
                    .exec();
        LOG.info(testPrefix, "write complete!");
        LOG.info(testPrefix, "write results: " + opResSet.getResultsByHandle());

        LOG.info(testPrefix, "starting read...");

        opResSet =
            ctrl
                .begin()
                    .read("READ")
                        .from()
                            .tbl(tbl)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with("everything")
                            .fam(fam)
                            .qual(qual)
                            .and()
                        .then()
                    .exec();

        LOG.info(testPrefix, "read complete! results (everything): ");

        for (CellDatum datum : opResSet.getReadResult("READ").getData("everything")) {
            LOG.info("retrieved: " + datum.getDatum());
        }

        LOG.info(testPrefix, "starting read...");

        opResSet =
            ctrl
                .begin()
                    .read("READ")
                        .from()
                            .tbl(tbl)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with("ge2")
                            .fam(fam)
                            .qual(qual)
                            .version()
                                .ge(2)
                                .and()
                            .and()
                        .then()
                    .exec();

        opResSet.getReadResult("READ");

        LOG.info(testPrefix, "read complete! results (ge2): ");

        for (CellDatum datum : opResSet.getReadResult("READ").getData("ge2")) {
            LOG.info("retrieved: " + datum.getDatum());
        }

        LOG.info(testPrefix, "starting read...");

        opResSet =
            ctrl
                .begin()
                    .read("READ")
                        .from()
                            .tbl(tbl)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with("le2")
                            .fam(fam)
                            .qual(qual)
                            .version()
                                .le(2)
                                .and()
                            .and()
                        .then()
                    .exec();

        opResSet.getReadResult("READ");

        LOG.info(testPrefix, "read complete! results (le2): ");

        for (CellDatum datum : opResSet.getReadResult("READ").getData("le2")) {
            LOG.info("retrieved: " + datum.getDatum());
        }

        LOG.info(testPrefix, "starting read...");

        opResSet =
            ctrl
                .begin()
                    .read("READ")
                        .from()
                            .tbl(tbl)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with("2")
                            .fam(fam)
                            .qual(qual)
                            .version(2)
                            .and()
                        .then()
                    .exec();

        LOG.info(testPrefix, "read complete! results (2): ");

        for (CellDatum datum : opResSet.getReadResult("READ").getData("2")) {
            LOG.info("retrieved: " + datum.getDatum());
        }
    }
}
