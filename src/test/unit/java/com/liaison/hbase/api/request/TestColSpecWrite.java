package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.impl.ColSpecWrite;
import com.liaison.hbase.api.request.impl.SpecState;
import com.liaison.hbase.api.request.impl.TableRowOpSpec;
import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.TableModel;
import com.liaison.hbase.model.ser.CellSerializer;
import com.liaison.hbase.testutil.TestingUtil;
import com.liaison.serialization.BytesUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.function.Consumer;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.25 17:36
 */
public class TestColSpecWrite {

    private static final Long VERSION_TEST = Long.valueOf(5);

    private static final CellSerializer ABC_PREFIX_SERIALIZER =
        (str)-> BytesUtil.toBytes("ABC-" + String.valueOf(str));
    private static final CellSerializer DEF_PREFIX_SERIALIZER =
        (str)->BytesUtil.toBytes("DEF-" + String.valueOf(str));
    private static final CellSerializer GHI_PREFIX_SERIALIZER =
        (str)->BytesUtil.toBytes("GHI-" + String.valueOf(str));
    private static final CellSerializer JKL_PREFIX_SERIALIZER =
        (str)->BytesUtil.toBytes("JKL-" + String.valueOf(str));

    private static final TableModel TABLE_TEST = TableModel.of(Name.of("TEST_TABLE"));
    private static final TableModel TABLE_TEST_WITHSER =
        TableModel
            .with(Name.of("TEST_TABLE"))
            .serializer(JKL_PREFIX_SERIALIZER)
            .build();
    private static final RowKey ROWKEY_TEST = RowKey.of("TEST_ROWKEY");
    private static final FamilyModel FAMILY_TEST = FamilyModel.of(Name.of("TEST_FAMILY"));
    private static final FamilyModel FAMILY_TEST_WITHSER =
        FamilyModel
            .with(Name.of("TEST_FAMILY"))
            .serializer(GHI_PREFIX_SERIALIZER)
            .build();
    private static final FamilyModel FAMILY_TEST_WITHSER_WITHQUALSER =
        FamilyModel
            .with(Name.of("TEST_FAMILY"))
            .serializer(GHI_PREFIX_SERIALIZER)
            .qual(QualModel
                      .with(Name.of("TEST_QUALIFIER"))
                      .serializer(DEF_PREFIX_SERIALIZER)
                      .build())
            .build();
    private static final QualModel QUAL_TEST = QualModel.of(Name.of("TEST_QUALIFIER"));
    private static final QualModel QUAL_TEST_WITH_SER =
        QualModel
            .with(Name.of("TEST_QUALIFIER"))
            .serializer(ABC_PREFIX_SERIALIZER)
            .build();
    private static final String DESC_TEST = "TEST DESCRIPTION";
    private static final Value VALUE_TEST = Value.of("TEST_VALUE");

    private static final String CONTENT_TEST = "1234567890";
    private static final String CONTENT_TEST_ABC_PREFIX = "ABC-1234567890";
    private static final String CONTENT_TEST_DEF_PREFIX = "DEF-1234567890";
    private static final String CONTENT_TEST_GHI_PREFIX = "GHI-1234567890";
    private static final String CONTENT_TEST_JKL_PREFIX = "JKL-1234567890";

    private static final TestingUtil.MockupConcreteTableRowOpSpec PARENT =
        TestingUtil.mockupTableRowOpSec(TABLE_TEST, ROWKEY_TEST);

    private static <P extends TableRowOpSpec<P>> ColSpecWrite<P> addSimpleReqProps(final ColSpecWrite<P> colSpecWrite) {
        return colSpecWrite.fam(FAMILY_TEST).qual(QUAL_TEST).value(VALUE_TEST);
    }

    /*
     * See note below regarding the methods here which take NoOpSpec as a parameter (versus the
     * ones which use the constant NoOpSpec PARENT instead).
     */
    private static <P extends TableRowOpSpec<P>> ColSpecWrite<P> buildColSpecWriteNoReqProps(final P parent) {
        return new ColSpecWrite<>(parent);
    }
    private static <P extends TableRowOpSpec<P>> ColSpecWrite<P> buildColSpecWrite(final P parent) {
        return addSimpleReqProps(buildColSpecWriteNoReqProps(parent));
    }

    /*
     * For most of the test cases, we can use one of the following two methods to build the
     * ColSpecRead to be tested using the already-built parent NoOpSpec. It is only necessary to
     * use the foregoing methods which accept a parent NoOpSpec as a parameter if the test requires
     * a change to the NoOpSpec parent itself (in particular, if the test requires that its state be
     * frozen).
     */
    private static ColSpecWrite<TestingUtil.MockupConcreteTableRowOpSpec> buildColSpecWriteNoReqProps() {
        return new ColSpecWrite<>(PARENT);
    }
    private static ColSpecWrite<TestingUtil.MockupConcreteTableRowOpSpec> buildColSpecWrite() {
        return addSimpleReqProps(buildColSpecWriteNoReqProps());
    }

    @Test
    public void testVersion() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWrite();
        Assert.assertSame(colSpecWrite, colSpecWrite.version(VERSION_TEST.longValue()));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getVersion(), VERSION_TEST);
    }

    @Test
    public void testTs() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWrite();
        Assert.assertSame(colSpecWrite, colSpecWrite.ts(VERSION_TEST.longValue()));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getTS(), VERSION_TEST);
    }

    @Test
    public void testValue() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWriteNoReqProps().fam(FAMILY_TEST).qual(QUAL_TEST);
        Assert.assertSame(colSpecWrite, colSpecWrite.value(VALUE_TEST));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getValue(), VALUE_TEST);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testValueAlreadyAssignedValue() throws Exception {
        buildColSpecWrite().value(VALUE_TEST);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testValueAlreadyAssignedEmpty() throws Exception {
        buildColSpecWriteNoReqProps()
            .fam(FAMILY_TEST)
            .qual(QUAL_TEST)
            .empty(Empty.getInstance())
            .value(VALUE_TEST);
    }

    @Test
    public void testEmpty() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWriteNoReqProps().fam(FAMILY_TEST).qual(QUAL_TEST);
        Assert.assertSame(colSpecWrite, colSpecWrite.empty(Empty.getInstance()));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getValue(), Empty.getInstance());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testEmptyAlreadyAssignedValue() throws Exception {
        buildColSpecWrite().empty(Empty.getInstance());
    }

    /**
     * Tests both the addition of content to ColSpecWrite and the serializer-selection logic.
     * @throws Exception
     */
    @Test
    public void testContent() throws Exception {
        final ColSpecWrite<?> cswQualSer;
        final ColSpecWrite<?> cswFamQualSer;
        final ColSpecWrite<?> cswFamSer;
        final ColSpecWrite<?> cswTableSer;

        // should use the qualifier-level serializer, which adds an ABC- prefix to the content
        cswQualSer =
            buildColSpecWriteNoReqProps(TestingUtil.mockupTableRowOpSec(TABLE_TEST_WITHSER,
                                                                        ROWKEY_TEST))
                .fam(FAMILY_TEST_WITHSER_WITHQUALSER)
                .qual(QUAL_TEST_WITH_SER);
        // should use the family-level, qualifier-specific serializer, which adds a DEF- prefix to
        // the content
        cswFamQualSer =
            buildColSpecWriteNoReqProps(TestingUtil.mockupTableRowOpSec(TABLE_TEST_WITHSER,
                                                                        ROWKEY_TEST))
                .fam(FAMILY_TEST_WITHSER_WITHQUALSER)
                .qual(QUAL_TEST);
        // should use the family-level default serializer, which adds a GHI- prefix to the content
        cswFamSer =
            buildColSpecWriteNoReqProps(TestingUtil.mockupTableRowOpSec(TABLE_TEST_WITHSER,
                                                                        ROWKEY_TEST))
                .fam(FAMILY_TEST_WITHSER)
                .qual(QUAL_TEST);
        // should use the table-level default serializer, which adds a JKL- prefix to the content
        cswTableSer =
            buildColSpecWriteNoReqProps(TestingUtil.mockupTableRowOpSec(TABLE_TEST_WITHSER,
                                                                        ROWKEY_TEST))
                .fam(FAMILY_TEST)
                .qual(QUAL_TEST);

        Assert.assertSame(cswQualSer, cswQualSer.content(CONTENT_TEST));
        Assert.assertSame(cswFamQualSer, cswFamQualSer.content(CONTENT_TEST));
        Assert.assertSame(cswFamSer, cswFamSer.content(CONTENT_TEST));
        Assert.assertSame(cswTableSer, cswTableSer.content(CONTENT_TEST));

        Assert.assertEquals(cswQualSer.getState(), SpecState.FLUID);
        Assert.assertEquals(cswFamQualSer.getState(), SpecState.FLUID);
        Assert.assertEquals(cswFamSer.getState(), SpecState.FLUID);
        Assert.assertEquals(cswTableSer.getState(), SpecState.FLUID);

        cswQualSer.freezeRecursive();
        cswFamQualSer.freezeRecursive();
        cswFamSer.freezeRecursive();
        cswTableSer.freezeRecursive();

        Assert.assertEquals(cswQualSer.getValue(), Value.of(CONTENT_TEST_ABC_PREFIX));
        Assert.assertEquals(cswFamQualSer.getValue(), Value.of(CONTENT_TEST_DEF_PREFIX));
        Assert.assertEquals(cswFamSer.getValue(), Value.of(CONTENT_TEST_GHI_PREFIX));
        Assert.assertEquals(cswTableSer.getValue(), Value.of(CONTENT_TEST_JKL_PREFIX));

        // some quick negative checks to verify that the equality logic is working correctly
        Assert.assertNotEquals(cswQualSer.getValue(), Value.of(CONTENT_TEST_JKL_PREFIX));
        Assert.assertNotEquals(cswFamQualSer.getValue(), Value.of(CONTENT_TEST_ABC_PREFIX));
        Assert.assertNotEquals(cswFamSer.getValue(), Value.of(CONTENT_TEST_DEF_PREFIX));
        Assert.assertNotEquals(cswTableSer.getValue(), Value.of(CONTENT_TEST_GHI_PREFIX));

        Assert.assertEquals(cswQualSer.getState(), SpecState.FROZEN);
        Assert.assertEquals(cswFamQualSer.getState(), SpecState.FROZEN);
        Assert.assertEquals(cswFamSer.getState(), SpecState.FROZEN);
        Assert.assertEquals(cswTableSer.getState(), SpecState.FROZEN);
    }

    @Test(expectedExceptions = SpecValidationException.class)
    public void testContentWithoutSerializer() throws Exception {
        final ColSpecWrite<?> colSpecWrite;
        colSpecWrite =
            buildColSpecWriteNoReqProps().fam(FAMILY_TEST).qual(QUAL_TEST).content(CONTENT_TEST);
        // fails validation
        colSpecWrite.freezeRecursive();
    }

    @Test
    public void testHandle() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWrite();
        Assert.assertSame(colSpecWrite, colSpecWrite.version(VERSION_TEST.longValue()));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getVersion(), VERSION_TEST);
    }

    @Test
    public void testFamQualValueHandle() throws Exception {
        final ColSpecWrite<?> colSpecWrite;

        colSpecWrite = buildColSpecWriteNoReqProps();
        Assert.assertSame(colSpecWrite, colSpecWrite.fam(FAMILY_TEST));
        Assert.assertSame(colSpecWrite, colSpecWrite.qual(QUAL_TEST));
        Assert.assertSame(colSpecWrite, colSpecWrite.value(VALUE_TEST));
        Assert.assertSame(colSpecWrite, colSpecWrite.handle(DESC_TEST));

        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getValue(), VALUE_TEST);
        Assert.assertEquals(colSpecWrite.getFamily(), FAMILY_TEST);
        Assert.assertEquals(colSpecWrite.getColumn(), QUAL_TEST);
        Assert.assertEquals(colSpecWrite.getHandle(), DESC_TEST);
    }

    @Test
    public void testAnd() throws Exception {
        Assert.assertSame(PARENT, buildColSpecWrite().and());
    }

    @Test
    public void testStateTransition() throws Exception {
        final TestingUtil.MockupConcreteTableRowOpSpec parent;
        ColSpecWrite<TestingUtil.MockupConcreteTableRowOpSpec> colSpecWrite;

        colSpecWrite = buildColSpecWrite();
        Assert.assertEquals(colSpecWrite.getState(), SpecState.FLUID);
        colSpecWrite.freezeRecursive();
        Assert.assertEquals(colSpecWrite.getState(), SpecState.FROZEN);

        parent = TestingUtil.mockupTableRowOpSec(TABLE_TEST, ROWKEY_TEST);

        colSpecWrite = buildColSpecWrite(parent);
        Assert.assertEquals(colSpecWrite.getState(), SpecState.FLUID);
        Assert.assertFalse(colSpecWrite.isFrozen());

        parent.freezeRecursive();

        Assert.assertEquals(colSpecWrite.getState(), SpecState.FROZEN);
        Assert.assertTrue(colSpecWrite.isFrozen());
    }

    private void testMutationAfterFreeze(final Consumer<ColSpecWrite<?>> mutator) throws Exception {
        final TestingUtil.MockupConcreteTableRowOpSpec parent;
        final ColSpecWrite<TestingUtil.MockupConcreteTableRowOpSpec> colSpecRead;

        parent = TestingUtil.mockupTableRowOpSec(TABLE_TEST, ROWKEY_TEST);
        colSpecRead = buildColSpecWrite(parent);

        parent.freezeRecursive();
        mutator.accept(colSpecRead);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationFamilyAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.fam(FAMILY_TEST));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationQualAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.qual(QUAL_TEST));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationHandleAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.handle(DESC_TEST));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationContentAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.content(CONTENT_TEST));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationValueAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.value(VALUE_TEST));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationEmptyAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.empty(Empty.getInstance()));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationVersionAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.version(VERSION_TEST.longValue()));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationTimestampAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.ts(VERSION_TEST.longValue()));
    }

    @Test
    public void testGetParent() throws Exception {
        Assert.assertSame(buildColSpecWrite().getParent(), PARENT);
    }
}
