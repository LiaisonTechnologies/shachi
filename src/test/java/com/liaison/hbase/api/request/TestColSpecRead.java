package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.api.request.impl.*;
import com.liaison.hbase.dto.FamilyQualifierPair;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.testutil.TestingUtil;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Consumer;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.20 11:07
 */
public class TestColSpecRead {

    private static final Long VERSION_MIN = Long.valueOf(5);
    private static final Long VERSION_MINPLUSONE = Long.valueOf(6);
    private static final Long VERSION_MAX = Long.valueOf(10);

    private static final FamilyModel FAMILY_TEST = FamilyModel.of(Name.of("TEST_FAMILY"));
    private static final FamilyModel FAMILY_TEST2 = FamilyModel.of(Name.of("SECRET_OTHER_FAMILY"));
    private static final QualModel QUAL_TEST = QualModel.of(Name.of("TEST_QUALIFIER"));
    private static final String DESC_TEST = "TEST DESCRIPTION";

    private static final NoOpSpec PARENT = TestingUtil.mockupNoOpSpec();

    /*
     * See note below regarding the methods here which take NoOpSpec as a parameter (versus the
     * ones which use the constant NoOpSpec PARENT instead).
     */
    private static ColSpecRead<NoOpSpec> buildColSpecReadNoFamily(final NoOpSpec parent) {
        return new ColSpecRead<>(parent);
    }
    private static ColSpecRead<NoOpSpec> buildColSpecRead(final NoOpSpec parent) {
        return buildColSpecReadNoFamily(parent).fam(FAMILY_TEST);
    }

    /*
     * For most of the test cases, we can use one of the following two methods to build the
     * ColSpecRead to be tested using the already-built parent NoOpSpec. It is only necessary to
     * use the foregoing methods which accept a parent NoOpSpec as a parameter if the test requires
     * a change to the NoOpSpec parent itself (in particular, if the test requires that its state be
     * frozen).
     */
    private static ColSpecRead<NoOpSpec> buildColSpecReadNoFamily() {
        return new ColSpecRead<>(PARENT);
    }
    private static ColSpecRead<NoOpSpec> buildColSpecRead() {
        return buildColSpecReadNoFamily().fam(FAMILY_TEST);
    }

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    private void verifyVersionSet(final ColSpecRead<?> colSpecRead, final ColSpecRead<?> colSpecReadFromAPI, final Long minInc, final Long maxExc) {
        final LongValueSpecFrozen colVer;

        Assert.assertSame(colSpecRead, colSpecReadFromAPI);
        colSpecRead.freezeRecursive();

        colVer = colSpecRead.getVersion();
        Assert.assertEquals(colVer.getLowerBoundInclusive(), minInc);
        Assert.assertEquals(colVer.getUpperBoundExclusive(), maxExc);
    }

    @Test
    public void testVersionRange() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecRead();
        verifyVersionSet(
                colSpecRead,
                colSpecRead.version().ge(VERSION_MIN).lt(VERSION_MAX).and(),
                VERSION_MIN,
                VERSION_MAX);
    }

    @Test
    public void testVersionSingle() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecRead();
        verifyVersionSet(
                colSpecRead,
                colSpecRead.version(VERSION_MIN),
                VERSION_MIN,
                VERSION_MINPLUSONE);
    }

    @Test
    public void testOptional() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecRead();
        Assert.assertSame(colSpecRead, colSpecRead.optional());
        colSpecRead.freezeRecursive();
        Assert.assertTrue(colSpecRead.isOptional());
    }

    @Test
    public void testToFQP() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        FamilyQualifierPair fqp;

        colSpecRead = buildColSpecRead();
        colSpecRead.qual(QUAL_TEST);

        fqp = colSpecRead.toFQP();
        Assert.assertEquals(
                fqp,
                FamilyQualifierPair.of(FAMILY_TEST, QUAL_TEST)
        );

        fqp = colSpecRead.toFQP(DESC_TEST);
        Assert.assertEquals(
                fqp,
                FamilyQualifierPair.of(FAMILY_TEST, QUAL_TEST, DESC_TEST)
        );
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testToFQPWithoutQualifier() throws Exception {
        buildColSpecRead().toFQP();
    }

    @Test(expectedExceptions = SpecValidationException.class)
    public void testValidateFailNoFamily() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecReadNoFamily();
        colSpecRead.freezeRecursive();
    }

    @Test
    public void testHandle() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecRead();
        Assert.assertSame(colSpecRead, colSpecRead.handle(DESC_TEST));
        colSpecRead.freezeRecursive();
        Assert.assertEquals(colSpecRead.getHandle(), DESC_TEST);
    }

    @Test
    public void testFam() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecReadNoFamily();
        Assert.assertSame(colSpecRead, colSpecRead.fam(FAMILY_TEST2));
        colSpecRead.freezeRecursive();
        Assert.assertEquals(FAMILY_TEST2, colSpecRead.getFamily());
    }

    @Test
    public void testQual() throws Exception {
        final ColSpecRead<NoOpSpec> colSpecRead;
        colSpecRead = buildColSpecRead();
        Assert.assertSame(colSpecRead, colSpecRead.qual(QUAL_TEST));
        colSpecRead.freezeRecursive();
        Assert.assertEquals(QUAL_TEST, colSpecRead.getColumn());
    }

    @Test
    public void testAnd() throws Exception {
        Assert.assertSame(PARENT, buildColSpecRead().and());
    }

    @Test
    public void testStateTransition() throws Exception {
        final NoOpSpec parent;
        final LongValueSpec<?> verSpec;
        ColSpecRead<NoOpSpec> colSpecRead;

        colSpecRead = buildColSpecRead();
        Assert.assertEquals(colSpecRead.getState(), SpecState.FLUID);
        colSpecRead.freezeRecursive();
        Assert.assertEquals(colSpecRead.getState(), SpecState.FROZEN);

        parent = TestingUtil.mockupNoOpSpec();
        colSpecRead = buildColSpecRead(parent);
        verSpec = colSpecRead.version().ge(VERSION_MIN).lt(VERSION_MAX);
        Assert.assertEquals(colSpecRead.getState(), SpecState.FLUID);
        Assert.assertFalse(colSpecRead.isFrozen());
        Assert.assertEquals(verSpec.getState(), SpecState.FLUID);
        Assert.assertFalse(verSpec.isFrozen());

        parent.freezeRecursive();

        Assert.assertEquals(colSpecRead.getState(), SpecState.FROZEN);
        Assert.assertTrue(colSpecRead.isFrozen());
        Assert.assertEquals(verSpec.getState(), SpecState.FROZEN);
        Assert.assertTrue(verSpec.isFrozen());
    }

    @Test
    public void testIsFrozen() throws Exception {
        final NoOpSpec parent;
        final LongValueSpec<?> verSpec;
        ColSpecRead<NoOpSpec> colSpecRead;

        colSpecRead = buildColSpecRead();
        Assert.assertFalse(colSpecRead.isFrozen());
        colSpecRead.freezeRecursive();
        Assert.assertTrue(colSpecRead.isFrozen());

        parent = TestingUtil.mockupNoOpSpec();
        colSpecRead = buildColSpecRead(parent);
        verSpec = colSpecRead.version().ge(VERSION_MIN).lt(VERSION_MAX);
        parent.freezeRecursive();
    }

    private void testMutationAfterFreeze(final Consumer<ColSpecRead<?>> mutator) throws Exception {
        final NoOpSpec parent;
        final ColSpecRead<NoOpSpec> colSpecRead;

        parent = TestingUtil.mockupNoOpSpec();
        colSpecRead = buildColSpecRead(parent);

        parent.freezeRecursive();
        mutator.accept(colSpecRead);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationFamilyAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.fam(FAMILY_TEST2));
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
    public void testMutationOptionalAfterFreeze() throws Exception {
        testMutationAfterFreeze(ColSpecRead::optional);
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationVersionSimpleAfterFreeze() throws Exception {
        testMutationAfterFreeze((spec)->spec.version(VERSION_MIN.longValue()));
    }
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMutationVersionRangeAfterFreeze() throws Exception {
        testMutationAfterFreeze(
                (spec)->spec.version().ge(VERSION_MIN.longValue()).lt(VERSION_MAX.longValue()));
    }

    @Test
    public void testGetParent() throws Exception {
        Assert.assertSame(buildColSpecRead().getParent(), PARENT);
    }
}
