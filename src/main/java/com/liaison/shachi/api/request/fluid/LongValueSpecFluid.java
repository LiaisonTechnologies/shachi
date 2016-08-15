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
package com.liaison.shachi.api.request.fluid;

/**
 * Specification for a range of long-integer (long) values.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P> Type of the parent/owning operation
 */
public interface LongValueSpecFluid<F extends LongValueSpecFluid<F,P>, P> {
    /**
     * <strong>Greater-Than</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V > value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>greater than</strong>
     * this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F gt(long value) throws ArithmeticException;
    /**
     * <strong>Greater-Than-Or-Equal</strong>: require that values <code>V</code> in the range
     * satisfy
     * <br><br>
     * <code>
     * V >= value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>greater than or equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F ge(long value) throws ArithmeticException;
    /**
     * <strong>Equal</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V == value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that the range consist of a single element, <strong>equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F eq(long value) throws ArithmeticException;
    /**
     * <strong>Lesser-Than</strong>: require that values <code>V</code> in the range satisfy
     * <br><br>
     * <code>
     * V < value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>lesser than</strong>
     * this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F lt(long value) throws ArithmeticException;
    /**
     * <strong>Lesser-Than-Or-Equal</strong>: require that values <code>V</code> in the range
     * satisfy
     * <br><br>
     * <code>
     * V <= value
     * </code>
     * <br><br>
     * where <code>value</code> is the value of the given parameter.
     * @param value (long) require that all values in the range be <strong>lesser than or equal
     * to</strong> this value
     * @return this instance (for fluent/chaining API)
     * @throws ArithmeticException if the given value is out-of-range
     */
    F le(long value) throws ArithmeticException;
}
