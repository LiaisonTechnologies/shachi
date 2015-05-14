package com.liaison.hbase.test.e2e;

import java.io.IOException;
import java.util.UUID;

import org.apache.hadoop.hbase.HBaseConfiguration;

import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.OpResultSet;
import com.liaison.hbase.context.MapRHBaseContext;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;

public class TestMapREnd2End {

    private static final String HANDLE_TESTWRITE_1 = "TEST-WRITE-1";
    private static final String HANDLE_TESTREAD_1 = "TEST-READ-1";
    private static final String TABLENAME_A = TestMapREnd2End.class.getSimpleName() + "_A";
    private static final String COLUMNFAMILY_a = "a";
    private static final String COLUMNQUAL_Z = "Z";
    private static final long TS_SAMPLE_1 = 1234567890;
    
    public void test1() {
        HBaseControl control = null;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        
        try {
            control =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .configProvider(() -> HBaseConfiguration.create())
                        .build());
            
            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();
            opResSet = 
                control
                    .now()
                    .write(HANDLE_TESTWRITE_1)
                        .on()
                            .tbl(TableModel.of(Name.of(TABLENAME_A)))
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FamilyModel.of(Name.of(COLUMNFAMILY_a)))
                            .qual(QualModel.of(Name.of(COLUMNQUAL_Z)))
                            .ts(0)
                            .value(Value.of(randomData))
                        .and()
                        .then()
                        .exec();
            System.out.println(opResSet.getResultsByHandle());
            
            opResSet =
                control
                    .now()
                    .read(HANDLE_TESTREAD_1)
                        .from()
                            .tbl(TableModel.of(Name.of(TABLENAME_A)))
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FamilyModel.of(Name.of(COLUMNFAMILY_a)))
                            .qual(QualModel.of(Name.of(COLUMNQUAL_Z)))
                        .and()
                        .atTime()
                            .gt(TS_SAMPLE_1 - 10)
                            .lt(TS_SAMPLE_1 + 10)
                        .and()
                        .then()
                        .exec();
            System.out.println(opResSet.getResultsByHandle());
        } catch (HBaseException hbExc) {
            hbExc.printStackTrace();
        } finally {
            try {
                if (control != null) {
                    control.close();
                }
            } catch (IOException ioExc) {
                ioExc.printStackTrace();
            }
        }
    }
    
    public static void main(final String[] arguments) {
        final TestMapREnd2End test;
        
        test = new TestMapREnd2End();
        test.test1();
    }
}
