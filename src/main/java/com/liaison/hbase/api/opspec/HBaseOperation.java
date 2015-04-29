package com.liaison.hbase.api.opspec;

import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;

public interface HBaseOperation<O extends HBaseOperation<O>> {
    O row(final RowKey rowKey);
    O tbl(final TableModel table);
    O fam(final FamilyModel family);
    O col(final FamilyModel family, final QualModel qual);
    LongValueSpec<O> ts();
}
