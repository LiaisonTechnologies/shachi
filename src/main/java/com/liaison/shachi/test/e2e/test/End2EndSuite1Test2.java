package com.liaison.shachi.test.e2e.test;

import com.liaison.commons.log.LogMeMaybe;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.response.OpResultSet;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.test.e2e.tools.Verify;

import java.util.UUID;

import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.COLUMNQUALS_ABC;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.FAM_MODEL_a;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.HANDLE_TESTREAD_2;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.HANDLE_TESTWRITE_2;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.TEST_MODEL_B;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.TS_SAMPLE_1;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:01
 */
public class End2EndSuite1Test2 implements End2EndTest {

    private static final LogMeMaybe LOG;
    static {
        LOG = new LogMeMaybe(End2EndSuite1Test2.class);
    }

    @Override
    public void runTest(final Verify verifier, final HBaseControl ctrl) throws HBaseException {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;

        testPrefix = "test2";
        try {
            LOG.info(testPrefix, "starting...");

            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();

            LOG.info(testPrefix, "starting write...");
            opResSet =
                ctrl
                    .begin()
                    .write(HANDLE_TESTWRITE_2)
                        .on()
                            .tbl(TEST_MODEL_B)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .withAllOf(COLUMNQUALS_ABC, (element, spec) -> {
                            spec.fam(FAM_MODEL_a);
                            spec.qual(QualModel.of(Name.of(element)));
                            spec.ts(TS_SAMPLE_1);
                            spec.value(Value.of(element + randomData));
                        })
                        .then()
                        .exec();
            LOG.info(testPrefix, "write complete!");
            LOG.info(testPrefix, "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix, "starting read...");
            opResSet =
                ctrl
                    .begin()
                    .read(HANDLE_TESTREAD_2)
                        .from()
                            .tbl(TEST_MODEL_B)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .version()
                                .eq(4)
                                /* TODO: fix the API to get rid of the ugly and-and sequence... */
                                .and()
                        .and()
                        .atTime()
                            .gt(TS_SAMPLE_1 - 10)
                            .lt(TS_SAMPLE_1 + 10)
                        .and()
                        .then()
                        .exec();

            LOG.info(testPrefix, "read complete!");
            LOG.info(testPrefix, "read results: " + opResSet.getResultsByHandle());
        } catch (HBaseException hbExc) {
            hbExc.printStackTrace();
        }
    }
}
