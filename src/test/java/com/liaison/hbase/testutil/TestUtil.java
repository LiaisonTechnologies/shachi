package com.liaison.hbase.testutil;

import com.liaison.hbase.api.request.impl.NoOpSpec;
import com.liaison.hbase.api.request.impl.OperationControllerDefault;
import com.liaison.hbase.context.HBaseContext;
import org.mockito.Mockito;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.25 16:59
 */
public final class TestUtil {

    private static final String DEFAULT_PARENT_HANDLE = "PARENT";

    public static NoOpSpec mockupNoOpSpec(final String specName) {
        final HBaseContext context;
        final OperationControllerDefault controller;

        context = Mockito.mock(HBaseContext.class);
        controller = Mockito.mock(OperationControllerDefault.class);
        return new NoOpSpec(specName, context, controller);
    }
    public static NoOpSpec mockupNoOpSpec() {
        return mockupNoOpSpec(DEFAULT_PARENT_HANDLE);
    }

    private TestUtil() {}
}
