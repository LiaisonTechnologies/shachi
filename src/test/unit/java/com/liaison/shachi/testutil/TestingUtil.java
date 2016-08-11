package com.liaison.shachi.testutil;

import com.liaison.shachi.api.request.impl.NoOpSpec;
import com.liaison.shachi.api.request.impl.OperationControllerDefault;
import com.liaison.shachi.api.request.impl.OperationSpec;
import com.liaison.shachi.api.request.impl.RowSpec;
import com.liaison.shachi.api.request.impl.TableRowOpSpec;
import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.dto.RowKey;
import com.liaison.shachi.model.TableModel;
import org.mockito.Mockito;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.25 16:59
 */
public final class TestingUtil {

    public static class MockupConcreteTableRowOpSpec extends TableRowOpSpec<MockupConcreteTableRowOpSpec> {
        private RowSpec<MockupConcreteTableRowOpSpec> rowSpec;

        public final MockupConcreteTableRowOpSpec tableRow(final RowSpec<MockupConcreteTableRowOpSpec> rowSpec) {
            this.rowSpec = rowSpec;
            return this;
        }

        @Override
        public RowSpec<MockupConcreteTableRowOpSpec> getTableRow() {
            return this.rowSpec;
        }

        @Override
        protected MockupConcreteTableRowOpSpec self() {
            return this;
        }
        @Override
        protected String prepareStrRepHeadline() {
            return "";
        }
        @Override
        protected boolean deepEquals(OperationSpec<?> otherOpSpec) {
            return true;
        }
        public MockupConcreteTableRowOpSpec(final String specName, final HBaseContext context, final OperationControllerDefault controller) {
            super(specName, context, controller);
        }
    }

    private static final String DEFAULT_PARENT_HANDLE = "PARENT";

    private static HBaseContext mockupHBaseContext() {
        return Mockito.mock(HBaseContext.class);
    }
    private static OperationControllerDefault mockupOpCtrl() {
        return Mockito.mock(OperationControllerDefault.class);
    }
    private static RowSpec<MockupConcreteTableRowOpSpec> mockupRowSpec(final MockupConcreteTableRowOpSpec tableRowOpSpec, final TableModel table, final RowKey rowKey) {
        return new RowSpec<>(tableRowOpSpec).tbl(table).row(rowKey);
    }

    public static MockupConcreteTableRowOpSpec mockupTableRowOpSec(final String specName, final TableModel table, final RowKey rowKey) {
        final MockupConcreteTableRowOpSpec trOpSpec;
        final RowSpec<MockupConcreteTableRowOpSpec> rowSpec;

        trOpSpec =
            new MockupConcreteTableRowOpSpec(specName, mockupHBaseContext(), mockupOpCtrl());
        rowSpec = mockupRowSpec(trOpSpec, table, rowKey);
        trOpSpec.tableRow(rowSpec);
        return trOpSpec;
    }
    public static MockupConcreteTableRowOpSpec mockupTableRowOpSec(final TableModel table, final RowKey rowKey) {
        return mockupTableRowOpSec(DEFAULT_PARENT_HANDLE, table, rowKey);
    }

    public static NoOpSpec mockupNoOpSpec(final String specName) {
        return new NoOpSpec(specName, mockupHBaseContext(), mockupOpCtrl());
    }
    public static NoOpSpec mockupNoOpSpec() {
        return mockupNoOpSpec(DEFAULT_PARENT_HANDLE);
    }

    private TestingUtil() {}
}
