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

package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.model.ColumnRange;
import com.liaison.shachi.model.FamilyHB;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.08 17:04
 */
public class GetColumnGrouping {

    private final Set<FamilyHB> familySet;
    private final Set<FamilyQualifierPair> fqpSet;
    private final Set<ColumnRange> columnRangeSet;

    public void addAllColumnRange(final Set<ColumnRange> colRangeSet) {
        Util.ensureNotNull(colRangeSet, this, "colRange", ColumnRange.class);
        this.columnRangeSet.addAll(colRangeSet);
    }
    public void addAllFQP(final Set<FamilyQualifierPair> fqpSet) {
        Util.ensureNotNull(fqpSet, this, "fqp", FamilyQualifierPair.class);
        this.fqpSet.addAll(fqpSet);
    }
    public void addAllFamily(final Set<FamilyHB> familySet) {
        Util.ensureNotNull(familySet, this, "family", FamilyHB.class);
        this.familySet.addAll(familySet);
    }

    public void addColumnRange(final ColumnRange colRange) {
        Util.ensureNotNull(colRange, this, "colRange", ColumnRange.class);
        this.columnRangeSet.add(colRange);
    }
    public void addFQP(final FamilyQualifierPair fqp) {
        Util.ensureNotNull(fqp, this, "fqp", FamilyQualifierPair.class);
        this.fqpSet.add(fqp);
    }
    public void addFamily(final FamilyHB family) {
        Util.ensureNotNull(family, this, "family", FamilyHB.class);
        this.familySet.add(family);
    }

    public Set<FamilyHB> getFamilySet() {
        return Collections.unmodifiableSet(this.familySet);
    }
    public Set<FamilyQualifierPair> getFQPSet() {
        return Collections.unmodifiableSet(this.fqpSet);
    }
    public Set<ColumnRange> getColumnRangeSet() {
        return Collections.unmodifiableSet(this.columnRangeSet);
    }

    public boolean hasFamilies() {
        return !this.familySet.isEmpty();
    }
    public boolean hasFQPs() {
        return !this.fqpSet.isEmpty();
    }
    public boolean hasColumnRanges() {
        return !this.columnRangeSet.isEmpty();
    }

    public GetColumnGrouping() {
        this.familySet = new HashSet<>();
        this.fqpSet = new HashSet<>();
        this.columnRangeSet = new HashSet<>();
    }
}
