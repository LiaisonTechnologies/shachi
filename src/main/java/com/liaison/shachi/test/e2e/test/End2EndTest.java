package com.liaison.shachi.test.e2e.test;

import com.liaison.shachi.HBaseControl;
import com.liaison.shachi.test.e2e.tools.Verify;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 15:02
 */
public interface End2EndTest {
    void runTest(final Verify verifier, final HBaseControl ctrl) throws Exception;
}
