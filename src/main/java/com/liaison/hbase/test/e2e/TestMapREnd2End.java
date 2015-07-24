/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.test.e2e;

import com.google.common.util.concurrent.ListenableFuture;
import com.liaison.commons.Util;
import com.liaison.hbase.HBaseControl;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.DirectoryPrefixedTableNamingStrategy;
import com.liaison.hbase.context.MapRHBaseContext;
import com.liaison.hbase.context.TableNamingStrategy;
import com.liaison.hbase.dto.CellDatum;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.HBaseException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.model.VersioningModel;
import com.liaison.hbase.resmgr.SimpleHBaseResourceManager;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestMapREnd2End implements Closeable {

    private static final Logger LOG;

    private static final String SYSPROP_PATH_MAPRTABLES = "PATH_MAPRTABLES";

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private static final String CONTEXT_ID_1 = "CONTEXT-1";
    private static final String HANDLE_TESTWRITE_1 = "TEST-WRITE-1";
    private static final String HANDLE_TESTREAD_1 = "TEST-READ-1";
    private static final String TABLENAME_A = TestMapREnd2End.class.getSimpleName() + "_A";
    private static final String COLUMNFAMILY_a = "a";
    private static final String COLUMNQUAL_Z = "Z";
    private static final long TS_SAMPLE_1 = 1234567890;
    
    private static final String CONTEXT_ID_2 = "CONTEXT-2";
    private static final String HANDLE_TESTWRITE_2 = "TEST-WRITE-2";
    private static final String HANDLE_TESTREAD_2 = "TEST-READ-2";
    private static final String TABLENAME_B = TestMapREnd2End.class.getSimpleName() + "_B";
    
    private static final String CONTEXT_ID_3 = "CONTEXT-3";
    private static final String HANDLE_TESTWRITE_3 = "TEST-WRITE-3";
    private static final String HANDLE_TESTREAD_3 = "TEST-READ-3";
    private static final String TABLENAME_C = TestMapREnd2End.class.getSimpleName() + "_C";
    
    private static final List<String> COLUMNQUALS_ABC;
    
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
    private static final TableModel TEST_MODEL_B =
        TableModel
            .with(Name.of(TABLENAME_B))
            .family(FAM_MODEL_a)
            .build();
    private static final TableModel TEST_MODEL_C =
        TableModel
            .with(Name.of(TABLENAME_C))
            .family(FAM_MODEL_a)
            .build();

    private static HBaseControl createControl(final TableNamingStrategy namingStrategy) {
        final HBaseControl hbc;

        if (namingStrategy == null) {
            hbc =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .id(DatasetSimulation.class.getSimpleName())
                        .configProvider(HBaseConfiguration::create)
                        .build(),
                    SimpleHBaseResourceManager.INSTANCE);
        } else {
            hbc =
                new HBaseControl(
                    MapRHBaseContext
                        .getBuilder()
                        .id(DatasetSimulation.class.getSimpleName())
                        .configProvider(HBaseConfiguration::create)
                        .tableNamingStrategy(namingStrategy)
                        .build(),
                    SimpleHBaseResourceManager.INSTANCE);
        }
        return hbc;
    }
    private static HBaseControl createControl() {
        return createControl(null);
    }
    
    static {
        final String[] alphabetArray;
        final List<String> alphabetList;
        LOG = LoggerFactory.getLogger(TestMapREnd2End.class);
        
        alphabetArray = new String[ALPHABET.length()];
        Arrays.setAll(alphabetArray,
                      (index) -> {
                          String str = "";
                          for (int iter = 0; iter < 10; iter++) {
                              str += ALPHABET.charAt(index);
                          }
                          return str;
                      });
        alphabetList = Arrays.asList(alphabetArray);
        Collections.shuffle(alphabetList);
        COLUMNQUALS_ABC = Collections.unmodifiableList(alphabetList);
    }

    private final HBaseControl ctrl;
    
    public void test1() {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        
        testPrefix = "[test1] ";
        try {
            LOG.info(testPrefix + "starting...");
            
            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();
            
            LOG.info(testPrefix + "starting write...");
            opResSet =
                this.ctrl
                    .begin()
                    .write(HANDLE_TESTWRITE_1)
                        .on()
                            .tbl(TEST_MODEL_A)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .qual(QUAL_MODEL_Z)
                            .ts(TS_SAMPLE_1)
                            .value(Value.of(randomData))
                        .and()
                        .then()
                        .exec();
            
            LOG.info(testPrefix + "write complete!");
            LOG.info(testPrefix + "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix + "starting read...");
            opResSet =
                this.ctrl
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
        }
    }
    
    public void test2() {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        
        testPrefix = "[test2] ";
        try {
            LOG.info(testPrefix + "starting...");
            
            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();
            
            LOG.info(testPrefix + "starting write...");
            opResSet =
                this.ctrl
                    .begin()
                    .write(HANDLE_TESTWRITE_2)
                        .on()
                            .tbl(TEST_MODEL_B)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .withAllOf(COLUMNQUALS_ABC, (element, spec) -> {
                            spec.fam(FAM_MODEL_a);
                            spec.qual(QualModel.of(Name.of(element)));
                            spec.ts(TS_SAMPLE_1);
                            spec.value(Value.of(element + randomData));
                        })
                        .then()
                        .exec();
            LOG.info(testPrefix + "write complete!");
            LOG.info(testPrefix + "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix + "starting read...");
            opResSet =
                this.ctrl
                    .begin()
                    .read(HANDLE_TESTREAD_2)
                        .from()
                            .tbl(TEST_MODEL_B)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                            .version()
                                .eq(4)
                                /* TODO: fix the API to get rid of the ugly and-and sequence... */
                                .and()
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
        }
    }
    
    public void test3() {
        final String testPrefix;
        ListenableFuture<OpResultSet> opResSetFuture;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        
        testPrefix = "[test3] ";
        try {
            LOG.info(testPrefix + "starting...");
            
            rowKeyStr = Long.toString(System.currentTimeMillis());
            randomData = UUID.randomUUID().toString();
            
            LOG.info(testPrefix + "starting write...");
            opResSetFuture = 
                this.ctrl
                    .begin()
                    .write(HANDLE_TESTWRITE_3)
                        .on()
                            .tbl(TEST_MODEL_C)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .withAllOf(COLUMNQUALS_ABC, (element, spec) -> {
                            spec.fam(FAM_MODEL_a);
                            spec.qual(QualModel.of(Name.of(element)));
                            spec.ts(TS_SAMPLE_1);
                            spec.value(Value.of(element + randomData));
                        })
                        .then()
                        .async()
                        .exec();
            LOG.info(testPrefix + "write started (async)!");
            opResSet = opResSetFuture.get();
            LOG.info(testPrefix + "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix + "starting read...");
            opResSet =
                this.ctrl
                    .begin()
                    .read(HANDLE_TESTREAD_3)
                        .from()
                            .tbl(TEST_MODEL_C)
                            .row(RowKey.of(rowKeyStr))
                        .and()
                        .with()
                            .fam(FAM_MODEL_a)
                        .and()
                        .atTime()
                            .gt(TS_SAMPLE_1 - 10)
                            .lt(TS_SAMPLE_1 + 10)
                        .and()
                        .then()
                        .exec();
            
            LOG.info(testPrefix + "read complete!");
            
            LOG.info(testPrefix + "read results: " + opResSet.getResultsByHandle());
        } catch (Exception exc) {
            LOG.error(testPrefix + " was BAD! and you should feel bad! " + exc, exc);
        }
    }

    public void test4() {
        final String testPrefix;
        OpResultSet opResSet;
        String rowKeyStr;
        String randomData;
        String tableName;
        FamilyModel fam;
        QualModel qual;
        TableModel tbl;

        testPrefix = "[test4] ";
        try {
            LOG.info(testPrefix + "starting...");

            randomData = UUID.randomUUID().toString();
            tableName = TestMapREnd2End.class.getSimpleName() + "-" + randomData;
            rowKeyStr = Long.toString(System.currentTimeMillis());
            fam = FamilyModel.of(Name.of("A"));
            qual =
                QualModel
                    .with(Name.of("B"))
                    .versionWith(VersioningModel.QUALIFIER_LATEST)
                    .build();
            tbl = TableModel.with(Name.of(tableName)).family(fam).build();

            LOG.info(testPrefix + "starting write...");
            opResSet =
                this.ctrl
                    .begin()
                        .write("WRITE")
                            .on()
                                .tbl(tbl)
                                .row(RowKey.of(rowKeyStr))
                                .and()
                            .with("ATVER1")
                                .fam(fam)
                                .qual(qual)
                                .version(1)
                                .value(Value.of(randomData))
                                .and()
                            .with("ATVER2")
                                .fam(fam)
                                .qual(qual)
                                .version(2)
                                .value(Value.of(randomData))
                                .and()
                            .with("ATVER3")
                                .fam(fam)
                                .qual(qual)
                                .version(3)
                                .value(Value.of(randomData))
                                .and()
                            .given()
                                .row(RowKey.of(rowKeyStr))
                                .fam(fam)
                                .qual(QualModel.of(Name.of("NON-EXISTENT")))
                                .empty()
                                .and()
                            .then()
                        .exec();
            LOG.info(testPrefix + "write complete!");
            LOG.info(testPrefix + "write results: " + opResSet.getResultsByHandle());

            LOG.info(testPrefix + "starting read...");

            opResSet =
                this.ctrl
                    .begin()
                        .read("READ")
                            .from()
                                .tbl(tbl)
                                .row(RowKey.of(rowKeyStr))
                                .and()
                            .with("everything")
                                .fam(fam)
                                .qual(qual)
                                .and()
                            .then()
                        .exec();

            LOG.info(testPrefix + "read complete! results (everything): ");

            for (CellDatum datum : opResSet.getReadResult("READ").getData("everything")) {
                LOG.info("retrieved: " + datum.getDatum());
            }

            LOG.info(testPrefix + "starting read...");

            opResSet =
                this.ctrl
                    .begin()
                        .read("READ")
                            .from()
                                .tbl(tbl)
                                .row(RowKey.of(rowKeyStr))
                                .and()
                            .with("ge2")
                                .fam(fam)
                                .qual(qual)
                                .version()
                                    .ge(2)
                                    .and()
                                .and()
                            .then()
                        .exec();

            opResSet.getReadResult("READ");

            LOG.info(testPrefix + "read complete! results (ge2): ");

            for (CellDatum datum : opResSet.getReadResult("READ").getData("ge2")) {
                LOG.info("retrieved: " + datum.getDatum());
            }

            LOG.info(testPrefix + "starting read...");

            opResSet =
                this.ctrl
                    .begin()
                        .read("READ")
                            .from()
                                .tbl(tbl)
                                .row(RowKey.of(rowKeyStr))
                                .and()
                            .with("le2")
                                .fam(fam)
                                .qual(qual)
                                .version()
                                    .le(2)
                                    .and()
                                .and()
                            .then()
                        .exec();

            opResSet.getReadResult("READ");

            LOG.info(testPrefix + "read complete! results (le2): ");

            for (CellDatum datum : opResSet.getReadResult("READ").getData("le2")) {
                LOG.info("retrieved: " + datum.getDatum());
            }

            LOG.info(testPrefix + "starting read...");

            opResSet =
                this.ctrl
                    .begin()
                        .read("READ")
                            .from()
                                .tbl(tbl)
                                .row(RowKey.of(rowKeyStr))
                                .and()
                            .with("2")
                                .fam(fam)
                                .qual(qual)
                                .version(2)
                                .and()
                            .then()
                        .exec();

            LOG.info(testPrefix + "read complete! results (2): ");

            for (CellDatum datum : opResSet.getReadResult("READ").getData("2")) {
                LOG.info("retrieved: " + datum.getDatum());
            }
        } catch (Exception exc) {
            LOG.error(testPrefix + " was BAD! and you should feel bad! " + exc, exc);
        }
    }

    @Override
    public void close() {
        this.ctrl.close();
    }

    public TestMapREnd2End() {
        final String tablesPathPrefix;

        tablesPathPrefix = Util.simplify(System.getProperty(SYSPROP_PATH_MAPRTABLES));
        if (tablesPathPrefix != null) {
            LOG.trace("Creating HBase control using directory naming prefix: " + tablesPathPrefix);
            this.ctrl = createControl(new DirectoryPrefixedTableNamingStrategy(tablesPathPrefix));
        } else {
            this.ctrl = createControl();
        }
    }

    public static void main(final String[] arguments) {
        try (final TestMapREnd2End test = new TestMapREnd2End()) {
            test.test1();
            test.test2();
            test.test4();
        }
    }
}
