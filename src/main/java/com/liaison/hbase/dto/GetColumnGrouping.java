package com.liaison.hbase.dto;

import com.liaison.commons.Util;
import com.liaison.hbase.model.ColumnRange;
import com.liaison.hbase.model.FamilyHB;

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
