package com.liaison.hbase.test.e2e.tools;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 13:26
 */
public interface Verify {
    void assertTrue(boolean condition, String message);
    void assertTrue(boolean condition);
    void assertFalse(boolean condition, String message);
    void assertFalse(boolean condition);
    void fail(String message, Throwable realCause);
    void fail(String message);
    void fail();
    void assertEquals(Object actual, Object expected, String message);
    void assertEquals(Object actual, Object expected);
    void assertEquals(String actual, String expected, String message);
    void assertEquals(String actual, String expected);
    void assertEquals(double actual, double expected, double delta, String message);
    void assertEquals(double actual, double expected, double delta);
    void assertEquals(float actual, float expected, float delta, String message);
    void assertEquals(float actual, float expected, float delta);
    void assertEquals(long actual, long expected, String message);
    void assertEquals(long actual, long expected);
    void assertEquals(boolean actual, boolean expected, String message);
    void assertEquals(boolean actual, boolean expected);
    void assertEquals(byte actual, byte expected, String message);
    void assertEquals(byte actual, byte expected);
    void assertEquals(char actual, char expected, String message);
    void assertEquals(char actual, char expected);
    void assertEquals(short actual, short expected, String message);
    void assertEquals(short actual, short expected);
    void assertEquals(int actual,  int expected, String message);
    void assertEquals(int actual, int expected);
    void assertNotNull(Object object);
    void assertNotNull(Object object, String message);
    void assertNull(Object object);
    void assertNull(Object object, String message);
    void assertSame(Object actual, Object expected, String message);
    void assertSame(Object actual, Object expected);
    void assertNotSame(Object actual, Object expected, String message);
    void assertNotSame(Object actual, Object expected);
    void assertEquals(Collection<?> actual, Collection<?> expected);
    void assertEquals(Collection<?> actual, Collection<?> expected, String message);
    void assertEquals(Iterator<?> actual, Iterator<?> expected);
    void assertEquals(Iterator<?> actual, Iterator<?> expected, String message);
    void assertEquals(Iterable<?> actual, Iterable<?> expected);
    void assertEquals(Iterable<?> actual, Iterable<?> expected, String message);
    void assertEquals(Object[] actual, Object[] expected, String message);
    void assertEqualsNoOrder(Object[] actual, Object[] expected, String message);
    void assertEquals(Object[] actual, Object[] expected);
    void assertEqualsNoOrder(Object[] actual, Object[] expected);
    void assertEquals(final byte[] actual, final byte[] expected);
    void assertEquals(final byte[] actual, final byte[] expected, final String message);
    void assertEquals(Set<?> actual, Set<?> expected);
    void assertEquals(Set<?> actual, Set<?> expected, String message);
    void assertEquals(Map<?, ?> actual, Map<?, ?> expected);
    void assertNotEquals(Object actual1, Object actual2, String message);
    void assertNotEquals(Object actual1, Object actual2);
    void assertNotEquals(float actual1, float actual2, float delta, String message);
    void assertNotEquals(float actual1, float actual2, float delta);
    void assertNotEquals(double actual1, double actual2, double delta, String message);
    void assertNotEquals(double actual1, double actual2, double delta);

}
