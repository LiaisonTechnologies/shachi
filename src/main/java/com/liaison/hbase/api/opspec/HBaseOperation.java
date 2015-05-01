package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.TableModel;

public interface HBaseOperation<O extends HBaseOperation<O>> {
    O row(final RowKey rowKey);
    O tbl(final TableModel table);
    LongValueSpec<O> ts();
}
