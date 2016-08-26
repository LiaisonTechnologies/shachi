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

package com.liaison.shachi.test.e2e;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.context.DirectoryPrefixedTableNamingStrategy;
import com.liaison.shachi.context.MapRHBaseContext;
import com.liaison.shachi.context.TableNamingStrategy;
import com.liaison.shachi.resmgr.SimpleHBaseResourceManager;
import com.liaison.shachi.test.e2e.test.End2EndTest;
import com.liaison.shachi.test.e2e.tools.AssertVerify;
import com.liaison.shachi.test.e2e.tools.Verify;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 13:07
 */
public class ManualTestRunner {

    private static final JitLog LOG;

    private static final String SYSPROP_PATH_MAPRTABLES = "PATH_MAPRTABLES";

    static {
        LOG = new JitLog(ManualTestRunner.class);
    }

    private final Verify verifier;
    private final HBaseControl ctrl;

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

    public void run(final List<End2EndTest> testList) {
        for (End2EndTest e2eTest : testList) {
            if (e2eTest != null) {
                try {
                    e2eTest.runTest(this.verifier, this.ctrl);
                } catch (Exception exc) {
                    LOG.error(exc.toString(), exc);
                }
            }
        }
    }
    public void run(End2EndTest... testList) {
        if ((testList != null) && (testList.length > 0)) {
            Arrays.asList(testList);
        }
    }

    public ManualTestRunner() {
        final String tablesPathPrefix;

        // log errors from test failures, but do not throw them as Errors
        this.verifier = new AssertVerify(EnumSet.of(AssertVerify.FailAction.WRITELOG));
        tablesPathPrefix = Util.simplify(System.getProperty(SYSPROP_PATH_MAPRTABLES));
        if (tablesPathPrefix != null) {
            LOG.trace("Creating HBase control using directory naming prefix: " + tablesPathPrefix);
            this.ctrl = createControl(new DirectoryPrefixedTableNamingStrategy(tablesPathPrefix));
        } else {
            this.ctrl = createControl();
        }
    }
}
