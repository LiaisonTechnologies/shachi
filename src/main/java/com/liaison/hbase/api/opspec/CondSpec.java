package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.dto.Empty;
import com.liaison.hbase.dto.NullableValue;
import com.liaison.hbase.dto.RowKey;
import com.liaison.hbase.dto.Value;
import com.liaison.hbase.exception.SpecValidationException;
import com.liaison.hbase.model.FamilyModel;
import com.liaison.hbase.model.QualModel;
import com.liaison.hbase.util.StringRepFormat;
import com.liaison.hbase.util.Util;

public final class CondSpec<P extends OperationSpec<P>> extends ColSpec<CondSpec<P>, P> implements Serializable {

    private static final long serialVersionUID = 328263884139551395L;
    
    private RowKey rowKey;
    private NullableValue value;

    @Override
    protected CondSpec<P> self() { return this; }

    @Override
    protected void validate() throws SpecValidationException {
        super.validate();
        Util.validateRequired(getRowKey(), this, "row", RowKey.class);
        Util.validateRequired(getFamily(), this, "fam", FamilyModel.class);
        Util.validateRequired(getColumn(), this, "qual", QualModel.class);
        Util.validateRequired(getValue(), this, "value/empty", NullableValue.class);
    }

    public RowKey getRowKey() {
        return this.rowKey;
    }
    public NullableValue getValue() {
        return this.value;
    }

    public CondSpec<P> row(final RowKey rowKey) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.rowKey = Util.validateExactlyOnceParam(rowKey, this, "rowKey", RowKey.class, this.rowKey);
        return self();
    }
    public CondSpec<P> value(final Value value) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(value, this, "value", Value.class, this.value);
        return self();
    }
    public CondSpec<P> empty(final Empty empty) throws IllegalStateException, IllegalArgumentException {
        prepMutation();
        this.value = Util.validateExactlyOnceParam(empty, this, "empty", Empty.class, this.value);
        return self();
    }

    @Override
    protected String prepareStrRepHeadline() {
        return "[given-condition]";
    }
    @Override
    protected void prepareStrRepAdditional(final StringBuilder strGen, final StringRepFormat format) {
        if (format == StringRepFormat.STRUCTURED) {
            if (this.rowKey != null) {
                Util.appendIndented(strGen, getDepth() + 1, "rowKey: ", this.rowKey, "\n");
            }
            if (this.value != null) {
                Util.appendIndented(strGen, getDepth() + 1, "value: ", this.value, "\n");
            }
        } else if (format == StringRepFormat.INLINE) {
            strGen.append("{");
            if (this.rowKey != null) {
                Util.append(strGen, "rowKey=", this.rowKey);
                if (this.value != null) {
                    strGen.append(",");
                }
            }
            if (this.value != null) {
                Util.append(strGen, "value=", this.value);
            }
            strGen.append("}");
        }
    }

    @Override
    protected int deepHashCode() {
        return (Util.hashCode(this.rowKey) ^ Util.hashCode(this.value));
    }

    @Override
    protected boolean deepEquals(final ColSpec<?, ?> otherColSpec) {
        final CondSpec<?> otherCondSpec;
        if (otherColSpec instanceof CondSpec) {
            otherCondSpec = (CondSpec<?>) otherColSpec;
            return (Util.refEquals(this.rowKey, otherCondSpec.rowKey)
                    && Util.refEquals(this.value, otherCondSpec.value));
        }
        return false;
    }

    public CondSpec(final P parent) {
        super(parent);
    }
}
