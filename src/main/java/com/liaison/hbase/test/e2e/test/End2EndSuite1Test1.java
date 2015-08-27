package com.liaison.hbase.test.e2e.test;

import com.liaison.commons.log.LogMeMaybe;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.test.e2e.tools.Verify;

import java.util.UUID;

import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.FAM_MODEL_a;
import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.HANDLE_TESTREAD_1;
import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.HANDLE_TESTWRITE_1;
import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.QUAL_MODEL_Z;
import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.TEST_MODEL_A;
import static com.liaison.hbase.test.e2e.setup.End2EndSuite1.TS_SAMPLE_1;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:01
 */
public class End2EndSuite1Test1 implements End2EndTest {

    private static final LogMeMaybe LOG;
    static {
        LOG = new LogMeMaybe(End2EndSuite1Test1.class);
    }

    @Override
    public void runTest(final Verify verifier, final HBaseControl ctrl) throws HBaseException {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;

        testPrefix = "test1";
        LOG.info(testPrefix, "starting...");

        rowKeyStr = Long.toString(System.currentTimeMillis());
        randomData = UUID.randomUUID().toString();

        LOG.info(testPrefix, "starting write...");
        opResSet =
            ctrl
                .begin()
                    .write(HANDLE_TESTWRITE_1)
                        .on()
                            .tbl(TEST_MODEL_A)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .qual(QUAL_MODEL_Z)
                            .ts(TS_SAMPLE_1)
                            .value(Value.of(randomData))
                            .and()
                        .then()
                    .exec();
        LOG.info(testPrefix, "write complete!");
        LOG.info(testPrefix, "write results: " + opResSet.getResultsByHandle());
        LOG.info(testPrefix, "starting read...");
        opResSet =
            ctrl
                .begin()
                    .read(HANDLE_TESTREAD_1)
                        .from()
                            .tbl(TEST_MODEL_A)
                            .row(RowKey.of(rowKeyStr))
                            .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .qual(QUAL_MODEL_Z)
                            .and()
                        .atTime()
                            .gt(TS_SAMPLE_1 - 10)
                            .lt(TS_SAMPLE_1 + 10)
                        .and()
                    .then()
                .exec();
        LOG.info(testPrefix, "read complete!");
        LOG.info(testPrefix, "read results: " + opResSet.getResultsByHandle());
    }
}
