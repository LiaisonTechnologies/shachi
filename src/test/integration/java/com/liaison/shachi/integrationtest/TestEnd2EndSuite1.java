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

package com.liaison.shachi.integrationtest;

import com.liaison.javabasics.logging.JitLog;
import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.context.MiniClusterTestHBaseContext;
import com.liaison.shachi.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.26 20:20
 */
public class TestEnd2EndSuite1 {

    private static final JitLog LOG;
    static {
        LOG = new JitLog(TestEnd2EndSuite1.class);
    }

    private HBaseTestingUtility hbTestUtil;
    private HBaseControl ctrl;

    @BeforeClass
    public void setup() throws Exception {
        LOG.info("System.getProperty('java.net.preferIPv4Stack')=" + System.getProperty("java.net.preferIPv4Stack"));
        LOG.info("Creating " + HBaseTestingUtility.class.getSimpleName() + "...");
        this.hbTestUtil = new HBaseTestingUtility();
        LOG.info(HBaseTestingUtility.class.getSimpleName() + " created; starting mini-cluster...");
        this.hbTestUtil.startMiniCluster();
        LOG.info("Mini-cluster started; building " + HBaseControl.class.getSimpleName() + "...");

        this.ctrl = new HBaseControl(
            MiniClusterTestHBaseContext
                .getBuilder()
                    .id(TestEnd2EndSuite1.class.getSimpleName())
                    .hbTestUtil(this.hbTestUtil)
                    .build(),
            SimpleHBaseResourceManager.INSTANCE
        );
        LOG.info(HBaseControl.class.getSimpleName() + "built");
    }

    @Test
    public void test() throws Exception {
        LOG.info("System.getProperty('java.net.preferIPv4Stack')=" + System.getProperty("java.net.preferIPv4Stack"));
        LOG.info("Closing " + HBaseControl.class.getSimpleName() + "...");
        this.ctrl.close();
        LOG.info(HBaseControl.class.getSimpleName() + "closed; shutting down mini-cluster...");
        this.hbTestUtil.shutdownMiniCluster();
        LOG.info("Mini-cluster shut down");
    }

}
