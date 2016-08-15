/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.liaison.shachi.test.e2e.test;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.api.response.OpResultSet;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.exception.HBaseException;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.test.e2e.tools.Verify;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.COLUMNQUALS_ABC;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.FAM_MODEL_a;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.HANDLE_TESTREAD_3;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.HANDLE_TESTWRITE_3;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.TEST_MODEL_C;
import static com.liaison.shachi.test.e2e.setup.End2EndSuite1.TS_SAMPLE_1;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:01
 */
public class End2EndSuite1Test3 implements End2EndTest {

    private static final JitLog LOG;
    static {
        LOG = new JitLog(End2EndSuite1Test3.class);
    }

    @Override
    public void runTest(final Verify verifier, final HBaseControl ctrl) throws InterruptedException, ExecutionException, HBaseException {
        final String testPrefix;
        ListenableFuture<OpResultSet> opResSetFuture;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;

        testPrefix = "test3";
        LOG.info(testPrefix, "starting...");

        rowKeyStr = Long.toString(System.currentTimeMillis());
        randomData = UUID.randomUUID().toString();

        LOG.info(testPrefix, "starting write...");
        opResSetFuture =
            ctrl
                .begin()
                .write(HANDLE_TESTWRITE_3)
                    .on()
                        .tbl(TEST_MODEL_C)
                        .row(RowKey.of(rowKeyStr))
                    .and()
                    .withAllOf(COLUMNQUALS_ABC, (element, spec) -> {
                        spec.fam(FAM_MODEL_a);
                        spec.qual(QualModel.of(Name.of(element)));
                        spec.ts(TS_SAMPLE_1);
                        spec.value(Value.of(element + randomData));
                    })
                    .then()
                    .async()
                    .exec();
        LOG.info(testPrefix, "write started (async)!");
        opResSet = opResSetFuture.get();
        LOG.info(testPrefix, "write results: " + opResSet.getResultsByHandle());
        LOG.info(testPrefix, "starting read...");
        opResSet =
            ctrl
                .begin()
                .read(HANDLE_TESTREAD_3)
                    .from()
                        .tbl(TEST_MODEL_C)
                        .row(RowKey.of(rowKeyStr))
                    .and()
                    .with()
                        .fam(FAM_MODEL_a)
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
