/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.liaison.shachi.test.e2e.tools;

import com.liaison.javabasics.logging.JitLog;
import org.testng.Assert;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 13:27
 */
public class AssertVerify implements Verify {
    public static enum FailAction {WRITELOG, THROW}

    private static final JitLog LOG;
    public static final EnumSet<FailAction> FAIL_ACTIONS_DEFAULT = EnumSet.of(FailAction.THROW);

    static {
        LOG = new JitLog(AssertVerify.class);
    }

    private final EnumSet<FailAction> failActions;

    private final void checkLogFailure(final Throwable t) {
        if (this.failActions.contains(FailAction.WRITELOG)) {
            LOG.error(String.valueOf(t), t);
        }
    }
    private final boolean isToThrow(final Throwable t) {
        return this.failActions.contains(FailAction.THROW);
    }

    private final void handleFailure(final Error t) {
        checkLogFailure(t);
        if (isToThrow(t)) {
            throw t;
        }
    }
    private final void handleFailure(final RuntimeException t) {
        checkLogFailure(t);
        if (isToThrow(t)) {
            throw t;
        }
    }

    public void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertTrue(boolean condition) {
        try {
            Assert.assertTrue(condition);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertFalse(boolean condition) {
        try {
            Assert.assertFalse(condition);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void fail(String message, Throwable realCause) {
        try {
            Assert.fail(message, realCause);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void fail(String message) {
        try {
            Assert.fail(message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void fail() {
        try {
            Assert.fail();
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Object actual, Object expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Object actual, Object expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(String actual, String expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(String actual, String expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(double actual, double expected, double delta, String message) {
        try {
            Assert.assertEquals(actual, expected, delta, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(double actual, double expected, double delta) {
        try {
            Assert.assertEquals(actual, expected, delta);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(float actual, float expected, float delta, String message) {
        try {
            Assert.assertEquals(actual, expected, delta, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(float actual, float expected, float delta) {
        try {
            Assert.assertEquals(actual, expected, delta);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(long actual, long expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(long actual, long expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(boolean actual, boolean expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(boolean actual, boolean expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(byte actual, byte expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(byte actual, byte expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(char actual, char expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(char actual, char expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(short actual, short expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(short actual, short expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(int actual, int expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(int actual, int expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotNull(Object object) {
        try {
            Assert.assertNotNull(object);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotNull(Object object, String message) {
        try {
            Assert.assertNotNull(object, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNull(Object object) {
        try {
            Assert.assertNull(object);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNull(Object object, String message) {
        try {
            Assert.assertNull(object, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertSame(Object actual, Object expected, String message) {
        try {
            Assert.assertSame(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertSame(Object actual, Object expected) {
        try {
            Assert.assertSame(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotSame(Object actual, Object expected, String message) {
        try {
            Assert.assertNotSame(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotSame(Object actual, Object expected) {
        try {
            Assert.assertNotSame(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Collection<?> actual, Collection<?> expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Collection<?> actual, Collection<?> expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Iterator<?> actual, Iterator<?> expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Iterator<?> actual, Iterator<?> expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Iterable<?> actual, Iterable<?> expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Iterable<?> actual, Iterable<?> expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Object[] actual, Object[] expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        try {
            Assert.assertEqualsNoOrder(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Object[] actual, Object[] expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        try {
            Assert.assertEqualsNoOrder(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(final byte[] actual, final byte[] expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(final byte[] actual, final byte[] expected, final String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Set<?> actual, Set<?> expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Set<?> actual, Set<?> expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertEquals(Map<?, ?> actual, Map<?, ?> expected) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(Object actual1, Object actual2, String message) {
        try {
            Assert.assertNotEquals(actual1, actual2, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(Object actual1, Object actual2) {
        try {
            Assert.assertNotEquals(actual1, actual2);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(float actual1, float actual2, float delta, String message) {
        try {
            Assert.assertNotEquals(actual1, actual2, delta, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(float actual1, float actual2, float delta) {
        try {
            Assert.assertNotEquals(actual1, actual2, delta);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(double actual1, double actual2, double delta, String message) {
        try {
            Assert.assertNotEquals(actual1, actual2, delta, message);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public void assertNotEquals(double actual1, double actual2, double delta) {
        try {
            Assert.assertNotEquals(actual1, actual2, delta);
        } catch (Error t) {
            handleFailure(t);
        } catch (RuntimeException t) {
            handleFailure(t);
        }
    }

    public AssertVerify(final EnumSet<FailAction> failActions) {
        if (failActions == null) {
            this.failActions = FAIL_ACTIONS_DEFAULT;
        } else {
            this.failActions = failActions;
        }
    }
}
