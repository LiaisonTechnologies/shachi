package com.liaison.shachi.model;

import com.liaison.javabasics.commons.Util;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.api.request.frozen.LongValueSpecFrozen;
import com.liaison.shachi.util.HBaseUtil;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.08 10:22
 */
public class ColumnRange implements Serializable {

    public static final class Builder {

        private FamilyHB family;
        private QualHB lower;
        private QualHB higher;
        private CompareFilter.CompareOp lowerComparator;
        private CompareFilter.CompareOp higherComparator;

        public Builder family(final FamilyHB family) {
            this.family = family;
            return this;
        }
        public Builder lower(final QualHB lower) {
            this.lower = lower;
            return this;
        }
        public Builder lowerComparator(final CompareFilter.CompareOp lowerComparator) {
            this.lowerComparator = lowerComparator;
            return this;
        }
        public Builder higher(final QualHB higher) {
            this.higher = higher;
            return this;
        }
        public Builder higherComparator(final CompareFilter.CompareOp higherComparator) {
            this.higherComparator = higherComparator;
            return this;
        }
        public ColumnRange build() {
            return new ColumnRange(this);
        }
        private Builder() {
            this.lower = null;
            this.higher = null;
        }
    }

    private static final EnumSet<CompareFilter.CompareOp> ALLOWED_LOWER_COMPARATORS =
        EnumSet.of(CompareFilter.CompareOp.GREATER, CompareFilter.CompareOp.GREATER_OR_EQUAL);

    private static final EnumSet<CompareFilter.CompareOp> ALLOWED_HIGHER_COMPARATORS =
        EnumSet.of(CompareFilter.CompareOp.LESS, CompareFilter.CompareOp.LESS_OR_EQUAL);

    public static ColumnRange from(final FamilyHB originFamily, final QualHB originQual, final VersioningModel verModel, final LongValueSpecFrozen lvSpec) {
        Long lowerBound;
        Long upperBound;
        final byte[] qualFromLower;
        final byte[] qualFromHigher;

        lowerBound = lvSpec.getLowerBoundInclusive();
        if (lowerBound == null) {
            lowerBound = Long.valueOf(lvSpec.getTypeMin());
        }
        upperBound = lvSpec.getUpperBoundExclusive();
        if (upperBound == null) {
            upperBound = Long.valueOf(lvSpec.getTypeMax());
        }

        qualFromLower =
            HBaseUtil.appendVersionToQual(originQual
                                              .getName()
                                              .getValue(DefensiveCopyStrategy.ALWAYS),
                                          lowerBound.longValue(),
                                          verModel);
        qualFromHigher =
            HBaseUtil.appendVersionToQual(originQual
                                              .getName()
                                              .getValue(DefensiveCopyStrategy.ALWAYS),
                                          upperBound.longValue(),
                                          verModel);
        /*
         * Depending on the HBase-level ordering which the versioning scheme in question is trying
         * to achieve, the absolute (lexical) ordering of the byte arrays produced by HBaseUtil.
         * appendVersionToQual may be inverted from the order of the versions themselves (i.e.
         * qualFromLower might be lexically higher than qualFromHigher). (The QUALIFIER_LATEST
         * versioning scheme, in particular, produces this effect.)
         *
         * In that case, when producing the final range, the qualifiers should be inverted. If so,
         * the "or-equal-to" part of the comparator which "lower bound INCLUSIVE" endpoint implies
         * is commuted over to the upper bound; the upper bound becomes an INCLUSIVE bound, and the
         * lower bound becomes an EXCLUSIVE one.
         */
        if (Bytes.compareTo(qualFromLower, qualFromHigher) <= 0) {
            // order preserved
            return
                getBuilder()
                    .family(originFamily)
                    .lower(QualModel.of(Name.of(qualFromLower, DefensiveCopyStrategy.NEVER)))
                    .lowerComparator(CompareFilter.CompareOp.GREATER_OR_EQUAL)
                    .higher(QualModel.of(Name.of(qualFromHigher, DefensiveCopyStrategy.NEVER)))
                    .higherComparator(CompareFilter.CompareOp.LESS)
                    .build();
        } else {
            // order inverted
            return
                getBuilder()
                    .family(originFamily)
                    .lower(QualModel.of(Name.of(qualFromHigher, DefensiveCopyStrategy.NEVER)))
                    .lowerComparator(CompareFilter.CompareOp.GREATER)
                    .higher(QualModel.of(Name.of(qualFromLower, DefensiveCopyStrategy.NEVER)))
                    .higherComparator(CompareFilter.CompareOp.LESS_OR_EQUAL)
                    .build();
        }
    }
    public static Builder getBuilder() {
        return new Builder();
    }

    private final FamilyHB family;
    private final QualHB lower;
    private final QualHB higher;
    private final CompareFilter.CompareOp lowerComparator;
    private final CompareFilter.CompareOp higherComparator;

    private String strRep;
    private Integer hc;

    public boolean contains(final QualHB qual) {
        final byte[] qualBytes;
        final byte[] lowerBytes;
        final byte[] upperBytes;
        int compare;

        qualBytes = qual.getName().getValue(DefensiveCopyStrategy.NEVER);

        lowerBytes = this.lower.getName().getValue(DefensiveCopyStrategy.NEVER);
        compare = Bytes.compareTo(lowerBytes, qualBytes);
        // condition: lower < value (exclusive)
        if ((this.lowerComparator == CompareFilter.CompareOp.LESS) && (compare >= 0)) {
            // fail because: lower >= value
            return false;
        }
        // condition: lower <= value (inclusive)
        if ((this.lowerComparator == CompareFilter.CompareOp.LESS_OR_EQUAL) && (compare > 0)) {
            // fail because: lower > value
            return false;
        }

        upperBytes = this.higher.getName().getValue(DefensiveCopyStrategy.NEVER);
        compare = Bytes.compareTo(upperBytes, qualBytes);
        // condition: upper > value (exclusive)
        if ((this.higherComparator == CompareFilter.CompareOp.GREATER) && (compare <= 0)) {
            // fail because: upper <= value
            return false;
        }
        // condition: upper >= value (inclusive)
        if ((this.higherComparator == CompareFilter.CompareOp.GREATER_OR_EQUAL) && (compare < 0)) {
            // fail because: upper < value
            return false;
        }
        return true;
    }

    public FamilyHB getFamily() {
        return this.family;
    }
    public QualHB getLower() {
        return this.lower;
    }
    public QualHB getHigher() {
        return this.higher;
    }
    public CompareFilter.CompareOp getLowerComparator() {
        return this.lowerComparator;
    }
    public CompareFilter.CompareOp getHigherComparator() {
        return this.higherComparator;
    }

    @Override
    public int hashCode() {
        int hCode;
        if (this.hc == null) {
            hCode = this.family.hashCode();
            hCode ^= this.lower.hashCode();
            hCode ^= this.higher.hashCode();
            this.hc = Integer.valueOf(hCode);
        }
        return this.hc.intValue();
    }

    @Override
    public boolean equals(final Object otherObj) {
        final ColumnRange otherQR;
        if (this == otherObj) {
            return true;
        } else if (otherObj instanceof ColumnRange) {
            otherQR = (ColumnRange) otherObj;
            return ((Util.refEquals(this.family, otherQR.family))
                    && (Util.refEquals(this.lowerComparator, otherQR.lowerComparator))
                    && (Util.refEquals(this.lower, otherQR.lower))
                    && (Util.refEquals(this.higherComparator, otherQR.higherComparator))
                    && (Util.refEquals(this.higher, otherQR.higher)));
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder strGen;
        if (this.strRep == null) {
            strGen = new StringBuilder();
            strGen.append(ColumnRange.class.getSimpleName());
            strGen.append("(family=");
            strGen.append(this.family);
            strGen.append(";qual=");
            strGen.append(this.lowerComparator);
            strGen.append(":");
            strGen.append(this.lower);
            strGen.append(",");
            strGen.append(this.higherComparator);
            strGen.append(":");
            strGen.append(this.higher);
            strGen.append(")");
            this.strRep = strGen.toString();
        }
        return this.strRep;
    }

    private ColumnRange(final Builder build) throws IllegalArgumentException {
        String logMsg;
        Util.ensureNotNull(build.family, this, "build.family", FamilyHB.class);
        Util.ensureNotNull(build.lower, this, "build.lower", QualHB.class);
        Util.ensureNotNull(build.higher, this, "build.higher", QualHB.class);
        Util.ensureNotNull(build.lowerComparator,
                           this,
                           "build.lowerComparator",
                           CompareFilter.CompareOp.class);
        Util.ensureNotNull(build.higherComparator,
                           this,
                           "build.higherComparator",
                           CompareFilter.CompareOp.class);
        this.family = build.family;
        this.lower = build.lower;
        this.higher = build.higher;
        this.lowerComparator = build.lowerComparator;
        this.higherComparator = build.higherComparator;

        if (!ALLOWED_LOWER_COMPARATORS.contains(this.lowerComparator)) {
            logMsg = "Lower qualifier comparator must be one of: "
                     + ALLOWED_LOWER_COMPARATORS
                     + "; found: "
                     + this.lowerComparator;
            throw new IllegalArgumentException(logMsg);
        }
        if (!ALLOWED_HIGHER_COMPARATORS.contains(this.higherComparator)) {
            logMsg = "Higher qualifier comparator must be one of: "
                     + ALLOWED_HIGHER_COMPARATORS
                     + "; found: "
                     + this.higherComparator;
            throw new IllegalArgumentException(logMsg);
        }
    }
}
