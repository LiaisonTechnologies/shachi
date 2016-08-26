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
package com.liaison.shachi.api.request.frozen;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public interface LongValueSpecFrozen {
    long getTypeMin();
    long getTypeMax();
    Long getLowerBoundInclusive();
    Long getUpperBoundExclusive();
    boolean isLowerBounded();
    boolean isUpperBounded();
    boolean isBounded();
    /**
     * If this long-value specification refers to a single value (rather than a range), then return
     * that value; otherwise, return null.
     * @return Long object indicating the single-element long-int to which this range refers, or
     * null if this range encompasses multiple elements.
     */
    Long singleValue();

    /**
     * Indicate whether this long-value specification refers to a single-element range. Equivalent
     * to {@code (singleValue() != null)}.
     * @return {@code (singleValue() != null)}; i.e. {@code true} if this is a single-element long-
     * int range, {@code false} otherwise.
     */
    boolean isSingleValue();
}
