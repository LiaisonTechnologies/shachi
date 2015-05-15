package com.liaison.hbase.test.e2e;

import java.io.IOException;
import java.util.UUID;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG;
    
    private static final String HANDLE_TESTWRITE_1 = "TEST-WRITE-1";
    private static final String HANDLE_TESTREAD_1 = "TEST-READ-1";
    private static final String TABLENAME_A = TestMapREnd2End.class.getSimpleName() + "_A";
    private static final String COLUMNFAMILY_a = "a";
    private static final String COLUMNQUAL_Z = "Z";
    private static final long TS_SAMPLE_1 = 1234567890;
    
    private static final QualModel QUAL_MODEL_Z =
        QualModel.with(Name.of(COLUMNQUAL_Z)).build();
    private static final FamilyModel FAM_MODEL_a =
        FamilyModel
            .with(Name.of(COLUMNFAMILY_a))
            .qual(QUAL_MODEL_Z)
            .build();
    private static final TableModel TEST_MODEL_A =
        TableModel
            .with(Name.of(TABLENAME_A))
            .family(FAM_MODEL_a)
            .build();
    
    static {
        LOG = LoggerFactory.getLogger(TestMapREnd2End.class);
    }
    
    public void test1() {
        final String testPrefix;
        HBaseControl control = null;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        
        testPrefix = "[test1] ";
        try {
            LOG.info(testPrefix + "starting...");
            control =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .configProvider(() -> HBaseConfiguration.create())
                        .build());
            

            LOG.info(testPrefix + "control: " + control);
            
            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();
            
            LOG.info(testPrefix + "starting write...");
            opResSet = 
                control
                    .begin()
                    .write(HANDLE_TESTWRITE_1)
                        .on()
                            .tbl(TEST_MODEL_A)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .qual(QUAL_MODEL_Z)
                            .ts(0)
                            .value(Value.of(randomData))
                        .and()
                        .then()
                        .exec();
            
            LOG.info(testPrefix + "write complete!");
            LOG.info(testPrefix + "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix + "starting read...");
            opResSet =
                control
                    .begin()
                    .read(HANDLE_TESTREAD_1)
                        .from()
                            .tbl(TEST_MODEL_A)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .qual(QUAL_MODEL_Z)
                        .and()
                        .atTime()
                            .gt(TS_SAMPLE_1 - 10)
                            .lt(TS_SAMPLE_1 + 10)
                        .and()
                        .then()
                        .exec();
            
            LOG.info(testPrefix + "read complete!");
            
            LOG.info(testPrefix + "read results: " + opResSet.getResultsByHandle());
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
