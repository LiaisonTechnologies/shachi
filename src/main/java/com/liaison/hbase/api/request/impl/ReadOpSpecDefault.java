/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.api.request.impl;

import com.liaison.commons.Util;
import com.liaison.hbase.api.request.ReadOpSpec;
import com.liaison.hbase.api.request.fluid.ColSpecReadFluid;
import com.liaison.hbase.api.request.frozen.LongValueSpecFrozen;
import com.liaison.hbase.api.response.OpResultSet;
import com.liaison.hbase.context.HBaseContext;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.model.VersioningModel;
import com.liaison.hbase.util.SpecUtil;
import com.liaison.hbase.util.StringRepFormat;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public final class ReadOpSpecDefault extends TableRowOpSpec<ReadOpSpecDefault> implements ReadOpSpec<OpResultSet>, Serializable {

    private static final long serialVersionUID = 1602390434837826147L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||

    private Integer maxEntriesPerFamily;
    private LongValueSpec<ReadOpSpecDefault> atTime;
    private final List<ColSpecRead<ReadOpSpecDefault>> withColumn;

    private EnumSet<VersioningModel> commonVersioningConfig;
    private LongValueSpecFrozen commonVersion;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FLUID                                                        ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public ReadOpSpecDefault atMost(final int maxEntriesPerFamily) throws IllegalArgumentException, IllegalStateException {
        String logMsg;
        if (maxEntriesPerFamily < 0) {
            logMsg =
                "Maximum number of entries to read per column family must be >= 0; specified: "
                + maxEntriesPerFamily;
            throw new IllegalArgumentException(logMsg);
        }
        prepMutation();
        Util.validateExactlyOnce("atMost", Integer.class, this.maxEntriesPerFamily);
        this.maxEntriesPerFamily = Integer.valueOf(maxEntriesPerFamily);
        return self();
    }

    @Override
    public LongValueSpec<ReadOpSpecDefault> atTime() throws IllegalStateException {
        prepMutation();
        Util.validateExactlyOnce("atTime", LongValueSpec.class, this.atTime);
        this.atTime = new LongValueSpec<>(this);
        return this.atTime;
    }
    
    @Override
    public RowSpec<ReadOpSpecDefault> from() throws IllegalArgumentException, IllegalStateException {
        final RowSpec<ReadOpSpecDefault> rowSpec;
        rowSpec = new RowSpec<>(this);
        setTableRow(rowSpec);
        return rowSpec;
    }
    
    @Override
    public ColSpecRead<ReadOpSpecDefault> with() throws IllegalStateException {
        final ColSpecRead<ReadOpSpecDefault> withCol;
        prepMutation();
        withCol = new ColSpecRead<>(this);
        this.withColumn.add(withCol);
        return withCol;
    }
    
    @Override
    public <X> ReadOpSpecDefault withAllOf(final Iterable<X> sourceData, final BiConsumer<? super X, ColSpecReadFluid<?>> dataToColumnGenerator) {
        ColSpecRead<ReadOpSpecDefault> withCol;
        prepMutation();
        if (sourceData != null) {
            for (X element : sourceData) {
                withCol = new ColSpecRead<>(this);
                dataToColumnGenerator.accept(element, new ColSpecReadConfined(withCol));
                this.withColumn.add(withCol);
            }
        }
        return self();
    }
    
    // ||----(instance methods: API: fluid)------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: API: FROZEN                                                       ||
    // ||----------------------------------------------------------------------------------------||

    @Override
    public EnumSet<VersioningModel> getCommonVersioningConfig() throws IllegalStateException {
        /*
         * Value is only populated after the validation step calls ensureCompatibleVersioning to
         * iterate through subordinate specifications to ensure that they use a common versioning
         * scheme/number if any of them use timestamp-based versioning. Therefore, this accessor
         * will always return null (meaninglessly) if the spec state is still unvalidated, i.e.
         * FLUID. In that case, throw IllegalStateException.
         */
        prepAccess("getCommonVersioningConfig");
        return this.commonVersioningConfig;
    }
    @Override
    public LongValueSpecFrozen getCommonVersion() throws IllegalStateException {
        /*
         * Value is only populated after the validation step calls ensureCompatibleVersioning to
         * iterate through subordinate specifications to ensure that they use a common versioning
         * scheme/number if any of them use timestamp-based versioning. Therefore, this accessor
         * will always return null (meaninglessly) if the spec state is still unvalidated, i.e.
         * FLUID. In that case, throw IllegalStateException.
         */
        prepAccess("getCommonVersion");
        return this.commonVersion;
    }
    @Override
    public Integer getMaxEntriesPerFamily() {
        return this.maxEntriesPerFamily;
    }
    @Override
    public LongValueSpec<ReadOpSpecDefault> getAtTime() {
        return this.atTime;
    }
    @Override
    public List<ColSpecRead<ReadOpSpecDefault>> getWithColumn() {
        return Collections.unmodifiableList(this.withColumn);
    }
    
    // ||----(instance methods: API: frozen)-----------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS: UTILITY                                                           ||
    // ||----------------------------------------------------------------------------------------||
    
    @Override
    protected ReadOpSpecDefault self() { return this; }

    /**
     * If there are multiple cells to be read, and if any of them use timestamp-based versioning,
     * then ensure that they all use a uniform versioning approach, as the timestamp setting on the
     * Get object used to execute the request will be common.
     * @throws SpecValidationException
     */
    private void ensureCompatibleVersioning() throws SpecValidationException {
        final String logMsg;
        final List<StatefulSpec<?, ?>> subordSpecList;
        ColSpecRead<?> readColSpec;
        QualModel colQualModel;
        FamilyModel colFamilyModel;
        boolean establishedVersioningConfigIsTimestampBased;
        EnumSet<VersioningModel> establishedVersioningConfig;
        LongValueSpec<?> establishedReadVersionSpec;
        EnumSet<VersioningModel> currentVersioningConfig;
        LongValueSpec<?> currentReadVersionSpec;

        subordSpecList = getSubordSpecList();
        establishedVersioningConfig = null;
        establishedReadVersionSpec = null;
        establishedVersioningConfigIsTimestampBased = false;

        /*
         * It is only necessary to check for versioning configuration + version number conflicts if
         * there is more than one read column in the list of subordinate specifications.
         */
        if (subordSpecList.size() > 1) {
            for (StatefulSpec<?, ?> spec : subordSpecList) {
                currentVersioningConfig = null;
                currentReadVersionSpec = null;

                /*
                 * Only examine instances of ColSpecRead (subordinate read columns) for this
                 * comparison. There should not be any subordinate columns of any other type, but
                 * since the collection of subordinate specifications is declared in a superclass
                 * high in the hierarchy, the type information is general.
                 */
                if (spec instanceof ColSpecRead) {
                    readColSpec = (ColSpecRead<?>) spec;

                    /*
                     * Get the version range/number specified for the current column.
                     */
                    currentReadVersionSpec = readColSpec.getVersion();

                    /*
                     * Determine the versioning configuration for the current read column spec. If
                     * a versioning configuration is declared at the qualifier level, use it;
                     * otherwise, default to the family level, if a configuration is defined there.
                     * Note that in some cases, neither the family model nor the qualifier model
                     * will define a versioning configuration, so currentVersioningConfig may
                     * remain null after this logic block.
                     */
                    colQualModel = readColSpec.getColumn();
                    if (colQualModel != null) {
                        currentVersioningConfig = colQualModel.getVersioning();
                    } else {
                        colFamilyModel = readColSpec.getFamily();
                        if (colFamilyModel != null) {
                            currentVersioningConfig = colFamilyModel.getVersioning();
                        }
                    }

                    /*
                     * If the "established" versioning configuration and version number for this
                     * read spec is not yet determined, then get it from this element (presumably,
                     * the first read-column specification subordinate to this instance). Otherwise,
                     * compare the current versioning configuration with the established one.
                     */
                    if (establishedVersioningConfig == null) {
                        establishedVersioningConfig = currentVersioningConfig;
                        establishedVersioningConfigIsTimestampBased =
                            VersioningModel.isTimestampBased(establishedVersioningConfig);
                        establishedReadVersionSpec = currentReadVersionSpec;
                    } else {
                        /*
                         * This validation method enforces that if ANY of read column specs specify
                         * a timestamp-based versioning scheme, then ALL of them must use the same
                         * versioning scheme.
                         *
                         * Consequently, the logic here is as follows:
                         *
                         * If either the "established" versioning config (i.e. that in the first
                         * column spec) is timestamp-based or the current versioning configuration
                         * is timestamp-based, then throw a SpecValidationException if either:
                         *    (a) the versioning configurations are NOT identical, or
                         *    (b) the version numbers (or ranges) are NOT identical
                         *
                         * Since this validation step ensures that all read columns use the same
                         * versioning configuration and version number, copy both the versioning
                         * config and the version number/range into this ReadOpSpec, for easier
                         * access during execution.
                         */
                        if ((establishedVersioningConfigIsTimestampBased
                             || VersioningModel.isTimestampBased(currentVersioningConfig))
                            &&
                            ((!Util.refEquals(establishedVersioningConfig,
                                              currentVersioningConfig))
                             || (!Util.refEquals(establishedReadVersionSpec,
                                                 currentReadVersionSpec)))) {
                            logMsg = "Failed to validate "
                                     + getClass().getSimpleName()
                                     + " (id:'"
                                     + getHandle()
                                     + "'). If multiple columns are specified, and any of them use"
                                     + " a timestamp-based versioning configuration (one of: "
                                     + VersioningModel.SET_TIMESTAMP
                                     + "), then all columns must specify an identical versioning "
                                     + "configuration and version number/range. Detected version "
                                     + "mismatch: first-column={config:"
                                     + establishedVersioningConfig
                                     + ",version:"
                                     + establishedReadVersionSpec
                                     + "}, current-column={config:"
                                     + establishedVersioningConfig
                                     + ",version:"
                                     + establishedReadVersionSpec
                                     + "}";
                            throw new SpecValidationException(SpecState.FLUID,
                                                              SpecState.FROZEN,
                                                              this,
                                                              logMsg);
                        }
                    }
                }
            }
        }
        this.commonVersioningConfig = establishedVersioningConfig;
        this.commonVersion = establishedReadVersionSpec;
    }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        SpecUtil.validateRequired(getTableRow(), this, "from", RowSpec.class);
        SpecUtil.validateAtLeastOne(getWithColumn(), this, "with", ColSpecRead.class);
        ensureCompatibleVersioning();
    }
    
    @Override
    protected String prepareStrRepHeadline() {
        return "[<<Operation>>:READ]";
    }
    
    @Override
    protected void prepareStrRep(final StringBuilder strGen, final StringRepFormat format) {
        final RowSpec<ReadOpSpecDefault> tableRow;
        tableRow = getTableRow();
        
        if (format == StringRepFormat.STRUCTURED) {
            if (tableRow != null) {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "from table/row: ",
                                    "\n",
                                    tableRow,
                                    "\n");
            }
            if (this.atTime != null) {
                Util.appendIndented(strGen,
                                    getDepth() + 1,
                                    "at (timestamp range): ",
                                    "\n",
                                    this.atTime,
                                    "\n");
            }
            if (this.withColumn.size() > 0) {
                Util.appendIndented(strGen, getDepth() + 1, "with column(s): ", "\n");
                for (ColSpecRead<ReadOpSpecDefault> colSpec : this.withColumn) {
                    Util.appendIndented(strGen, getDepth() + 1, colSpec);
                }
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (tableRow != null) {
                Util.append(strGen, "from=", tableRow);
                if ((this.atTime != null) && (this.withColumn.size() > 0)) {
                    strGen.append(",");
                }
            }
            if (this.atTime != null) {
                Util.append(strGen, "@ts=", this.atTime);
                if (this.withColumn.size() > 0) {
                    strGen.append(",");
                }
            }
            if (this.withColumn.size() > 0) {
                Util.append(strGen, "col=", this.withColumn);
            }
            strGen.append("}");
        }
    }
    
    @Override
    protected boolean deepEquals(final OperationSpec<?> otherOpSpec) {
        final ReadOpSpecDefault otherReadSpec;
        if (otherOpSpec instanceof ReadOpSpecDefault) {
            otherReadSpec = (ReadOpSpecDefault) otherOpSpec;
            /*
             * Equality checks ordered from least to most expensive, to allow for relatively quick
             * short-circuiting
             */
            return ((Util.refEquals(this.atTime, otherReadSpec.atTime))
                    &&
                    (Util.refEquals(getTableRow(), otherReadSpec.getTableRow()))
                    &&
                    (Util.refEquals(this.withColumn, otherReadSpec.withColumn)));
        }
        return false;
    }
    
    // ||----(instance methods: utility)---------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public ReadOpSpecDefault(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(handle, context, parent);
        this.withColumn = new LinkedList<>();
        this.atTime = null;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
