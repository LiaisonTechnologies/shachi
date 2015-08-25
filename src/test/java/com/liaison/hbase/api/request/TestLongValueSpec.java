/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request;

import com.liaison.hbase.api.request.impl.LongValueSpec;
import com.liaison.hbase.api.request.impl.NoOpSpec;
import com.liaison.hbase.testutil.TestUtil;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.function.BiConsumer;

public class TestLongValueSpec {

    private static final long NONBOUNDARY_LONG_LOW = 5;
    private static final long NONBOUNDARY_LONG_HIGH = 10;
    
    private static LongValueSpec<NoOpSpec> buildLongValueSpec() {
        return new LongValueSpec<NoOpSpec>(TestUtil.mockupNoOpSpec());
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testGTBoundary() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.gt(Long.MAX_VALUE);
    }
    
    @Test()
    public void testGT() {
        LongValueSpec<NoOpSpec> lvs;
        
        lvs = buildLongValueSpec();
        lvs.gt(NONBOUNDARY_LONG_HIGH);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH + 1),
                            "Failed greater-than (>) minimum-test on non-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed greater-than (>) null-maximum-test on non-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.gt(Long.MIN_VALUE);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(Long.MIN_VALUE + 1),
                            "Failed greater-than (>) minimum-test on lower-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed greater-than (>) null-maximum-test on lower-boundary value");
    }
    
    @Test()
    public void testGE() {
        LongValueSpec<NoOpSpec> lvs;
        
        lvs = buildLongValueSpec();
        lvs.ge(NONBOUNDARY_LONG_HIGH);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH),
                            "Failed greater-than-or-equal (>=) minimum-test on non-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed greater-than-or-equal (>=) null-maximum-test on non-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.ge(Long.MIN_VALUE);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed greater-than-or-equal (>=) null-minimum-test on lower-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed greater-than-or-equal (>=) null-maximum-test on lower-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.ge(Long.MAX_VALUE);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(Long.MAX_VALUE),
                            "Failed greater-than-or-equal (>=) minimum-test on upper-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed greater-than-or-equal (>=) null-maximum-test on upper-boundary value");
    }
    
    @Test()
    public void testEQ() {
        LongValueSpec<NoOpSpec> lvs;
        
        lvs = buildLongValueSpec();
        lvs.eq(NONBOUNDARY_LONG_HIGH);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH),
                            "Failed equal (==) minimum-test on non-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH + 1),
                            "Failed equal (==) maximum-test on non-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.eq(Long.MIN_VALUE);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed equal (==) null-minimum-test on lower-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(Long.MIN_VALUE + 1),
                            "Failed equal (==) maximum-test on lower-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.eq(Long.MAX_VALUE);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            Long.valueOf(Long.MAX_VALUE),
                            "Failed equal (==) minimum-test on upper-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed equal (==) null-maximum-test on upper-boundary value");
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testLTBoundary() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.lt(Long.MIN_VALUE);
    }
    
    @Test()
    public void testLT() {
        LongValueSpec<NoOpSpec> lvs;
        
        lvs = buildLongValueSpec();
        lvs.lt(NONBOUNDARY_LONG_HIGH);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed lesser-than (<) null-minimum-test on non-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH),
                            "Failed lesser-than (<) maximum-test on non-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.lt(Long.MAX_VALUE);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed lesser-than (<) null-minimum-test on upper-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(Long.MAX_VALUE),
                            "Failed lesser-than (<) maximum-test on non-boundary value");
    }
    
    @Test()
    public void testLE() {
        LongValueSpec<NoOpSpec> lvs;
        
        lvs = buildLongValueSpec();
        lvs.le(NONBOUNDARY_LONG_HIGH);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed lesser-than-or-equal (<=) null-minimum-test on non-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(NONBOUNDARY_LONG_HIGH + 1),
                            "Failed lesser-than-or-equal (<=) minimum-test on non-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.le(Long.MIN_VALUE);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed lesser-than-or-equal (<=) null-maximum-test on lower-boundary value");
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            Long.valueOf(Long.MIN_VALUE + 1),
                            "Failed lesser-than-or-equal (<=) minimum-test on lower-boundary value");
        
        lvs = buildLongValueSpec();
        lvs.le(Long.MAX_VALUE);
        Assert.assertNull(lvs.getLowerBoundInclusive(),
                          "Failed lesser-than-or-equal (<=) null-minimum-test on upper-boundary value");
        Assert.assertNull(lvs.getUpperBoundExclusive(),
                          "Failed lesser-than-or-equal (<=) null-maximum-test on upper-boundary value");
    }
    
    private static String buildAssertSingleRangeTestSummaryMessage(final String firstSetterName, final String secondSetterName, final long firstArg, final long secondArg) {
        return ("range("
                + firstSetterName
                + "="
                + firstArg
                + ","
                + secondSetterName
                + "="
                + secondArg
                + ")");
    }
    private static void assertSingleRangeTest(final LongValueSpec<?> lvs, final String firstSetterName, final String secondSetterName, final long firstArg, final long secondArg, final Long lowerBound, final Long upperBound) {
        final String summaryMsg;
        
        summaryMsg =
            buildAssertSingleRangeTestSummaryMessage(firstSetterName,
                                                     secondSetterName,
                                                     firstArg,
                                                     secondArg);
        Assert.assertEquals(lvs.getLowerBoundInclusive(),
                            lowerBound,
                            ("Failed " + summaryMsg + ": lower-bound-test"));
        Assert.assertEquals(lvs.getUpperBoundExclusive(),
                            upperBound,
                            ("Failed " + summaryMsg + ": upper-bound-test"));
    }
    
    private void singleRangeTest(final BiConsumer<LongValueSpec<?>, Long> firstArgSetter, final String firstSetterName, final BiConsumer<LongValueSpec<?>, Long> secondArgSetter, final String secondSetterName, final long firstArg, final long secondArg, final Long lowerBound, final Long upperBound) {
        LongValueSpec<?> lvs;

        lvs = buildLongValueSpec();
        firstArgSetter.accept(lvs, Long.valueOf(firstArg));
        secondArgSetter.accept(lvs, Long.valueOf(secondArg));
        assertSingleRangeTest(lvs,
                              firstSetterName,
                              secondSetterName,
                              firstArg,
                              secondArg,
                              lowerBound,
                              upperBound);

        /*
         * Operations on the LongValueSpec should be commutative, so reverse the order of the
         * setter calls and argument checks, but leave the bounds as they are
         */
        lvs = buildLongValueSpec();
        secondArgSetter.accept(lvs, Long.valueOf(secondArg));
        firstArgSetter.accept(lvs, Long.valueOf(firstArg));
        assertSingleRangeTest(lvs,
                              secondSetterName,
                              firstSetterName,
                              secondArg,
                              firstArg,
                              lowerBound,
                              upperBound);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGTLTEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.gt(NONBOUNDARY_LONG_HIGH); // >5 -> lower-bound-inc:6
        lvs.lt(NONBOUNDARY_LONG_HIGH + 1); // <6 -> upper-bound-exc:6
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLTGTEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.lt(NONBOUNDARY_LONG_HIGH + 1);
        lvs.gt(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGTLEEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.gt(NONBOUNDARY_LONG_HIGH); // >5 -> lower-bound-inc:6
        lvs.le(NONBOUNDARY_LONG_HIGH); // <=5 -> upper-bound-exc:6
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLEGTEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.le(NONBOUNDARY_LONG_HIGH);
        lvs.gt(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGELTEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.ge(NONBOUNDARY_LONG_HIGH); // >=5 -> lower-bound-inc:5
        lvs.lt(NONBOUNDARY_LONG_HIGH); // <5 -> upper-bound-exc:5
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLTGEEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.lt(NONBOUNDARY_LONG_HIGH);
        lvs.ge(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGELEEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.ge(NONBOUNDARY_LONG_HIGH + 1); // >=6 -> lower-bound-inc:6
        lvs.le(NONBOUNDARY_LONG_HIGH); // <=5 -> upper-bound-exc:6
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLEGEEmptyRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.le(NONBOUNDARY_LONG_HIGH);
        lvs.ge(NONBOUNDARY_LONG_HIGH + 1);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGTLTInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.gt(NONBOUNDARY_LONG_HIGH); // >10 -> lower-bound-inc:11
        lvs.lt(NONBOUNDARY_LONG_LOW); // <5 -> upper-bound-exc:5
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLTGTInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.lt(NONBOUNDARY_LONG_LOW);
        lvs.gt(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGTLEInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.gt(NONBOUNDARY_LONG_HIGH); // >10 -> lower-bound-inc:11
        lvs.le(NONBOUNDARY_LONG_LOW); // <=5 -> upper-bound-exc:6
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLEGTInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.le(NONBOUNDARY_LONG_LOW);
        lvs.gt(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGELTInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.ge(NONBOUNDARY_LONG_HIGH); // >=10 -> lower-bound-inc:10
        lvs.lt(NONBOUNDARY_LONG_LOW); // <5 -> upper-bound-exc:5
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLTGEInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.lt(NONBOUNDARY_LONG_LOW);
        lvs.ge(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesGELEInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.ge(NONBOUNDARY_LONG_HIGH); // >=10 -> lower-bound-inc:10
        lvs.le(NONBOUNDARY_LONG_LOW); // <=5 -> upper-bound-exc:6
    }
    @Test(expectedExceptions = ArithmeticException.class)
    public void testBadRangesLEGEInvertedRange() throws ArithmeticException {
        LongValueSpec<NoOpSpec> lvs;
        lvs = buildLongValueSpec();
        lvs.le(NONBOUNDARY_LONG_LOW);
        lvs.ge(NONBOUNDARY_LONG_HIGH);
    }
    
    @Test
    public void testRanges() {
        // range: (5,10) -> low-bound-inc: 6, high-bound-exc: 10
        singleRangeTest(LongValueSpec::gt,
                        "gt",
                        LongValueSpec::lt,
                        "lt",
                        NONBOUNDARY_LONG_LOW,
                        NONBOUNDARY_LONG_HIGH,
                        Long.valueOf(NONBOUNDARY_LONG_LOW + 1),
                        Long.valueOf(NONBOUNDARY_LONG_HIGH));
        // range: [5,10) -> low-bound-inc: 5, high-bound-exc: 10
        singleRangeTest(LongValueSpec::ge,
                        "ge",
                        LongValueSpec::lt,
                        "lt",
                        NONBOUNDARY_LONG_LOW,
                        NONBOUNDARY_LONG_HIGH,
                        Long.valueOf(NONBOUNDARY_LONG_LOW),
                        Long.valueOf(NONBOUNDARY_LONG_HIGH));
        // range: (5,10] -> low-bound-inc: 6, high-bound-exc: 11
        singleRangeTest(LongValueSpec::gt,
                        "gt",
                        LongValueSpec::le,
                        "le",
                        NONBOUNDARY_LONG_LOW,
                        NONBOUNDARY_LONG_HIGH,
                        Long.valueOf(NONBOUNDARY_LONG_LOW + 1),
                        Long.valueOf(NONBOUNDARY_LONG_HIGH + 1));
        // range: [5,10] -> low-bound-inc: 5, high-bound-exc: 11
        singleRangeTest(LongValueSpec::ge,
                        "ge",
                        LongValueSpec::le,
                        "le",
                        NONBOUNDARY_LONG_LOW,
                        NONBOUNDARY_LONG_HIGH,
                        Long.valueOf(NONBOUNDARY_LONG_LOW),
                        Long.valueOf(NONBOUNDARY_LONG_HIGH + 1));
    }
}
