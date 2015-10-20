package com.liaison.hbase.test.e2e.test;

import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.test.e2e.tools.Verify;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:02
 */
public interface End2EndTest {
    void runTest(final Verify verifier, final HBaseControl ctrl) throws Exception;
}
