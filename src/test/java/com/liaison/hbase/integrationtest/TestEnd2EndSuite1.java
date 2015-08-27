package com.liaison.hbase.integrationtest;

import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.HBaseStart;
import com.liaison.hbase.context.MiniClusterTestHBaseContext;
import com.liaison.hbase.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.26 20:20
 */
public class TestEnd2EndSuite1 {

    private HBaseTestingUtility hbTestUtil;
    private HBaseStart ctrl;

    @BeforeClass
    public void setup() throws Exception {
        this.hbTestUtil = new HBaseTestingUtility();
        this.hbTestUtil.startMiniCluster();

        this.ctrl = new HBaseControl(
            MiniClusterTestHBaseContext
                .getBuilder()
                    .id(TestEnd2EndSuite1.class.getSimpleName())
                    .hbTestUtil(this.hbTestUtil)
                    .build(),
            SimpleHBaseResourceManager.INSTANCE
        );
    }

    @Test
    public void test() throws Exception {

    }
}
