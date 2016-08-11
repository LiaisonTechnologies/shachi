/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.util;

import com.liaison.javabasics.commons.Uninstantiable;
import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.api.request.frozen.ColSpecFrozen;
import com.liaison.shachi.api.request.impl.SpecState;
import com.liaison.shachi.api.request.impl.StatefulSpec;
import com.liaison.shachi.dto.FamilyQualifierPair;
import com.liaison.shachi.exception.SpecValidationException;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.model.TableHB;
import com.liaison.shachi.model.VersioningModel;
import com.liaison.shachi.model.ser.CellDeserializer;
import com.liaison.shachi.model.ser.CellSerializable;
import com.liaison.shachi.model.ser.CellSerializer;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public final class SpecUtil extends Uninstantiable {

    public static <X> void validateRequired(final X specValue, final StatefulSpec<?, ?> spec, final String fieldName, final Class<? super X> fieldType) throws SpecValidationException {
        try {
            Util.ensureNotNull(specValue, spec, fieldName, fieldType);
        } catch (IllegalArgumentException iaExc) {
            throw new SpecValidationException(spec.getState(), SpecState.FROZEN, spec, iaExc.getMessage());
        }
    }
    public static <X> void validateAtLeastOne(final Collection<X> specValue, final StatefulSpec<?, ?> spec, final String fieldName, final Class<?> fieldType) throws SpecValidationException {
        try {
            Util.ensureNotNull(specValue, spec, fieldName);
        } catch (IllegalArgumentException iaExc) {
            throw new SpecValidationException(spec.getState(), SpecState.FROZEN, spec, iaExc.getMessage());
        }
        if (specValue.size() <= 0) {
            throw new SpecValidationException(spec.getState(),
                    SpecState.FROZEN,
                    spec,
                    ("At least one "
                            + fieldName
                            + " ("
                            + fieldType.getSimpleName()
                            + ") is required"));
        }
    }

    /**
     * Determine the versioning configuration for the current read column spec. If a versioning
     * configuration is declared at the qualifier level, use it; otherwise, default to the family
     * level, if a configuration is defined there. Note that in some cases, neither the family
     * model nor the qualifier model will define a versioning configuration, so this method may
     * return null.
     * @param colFam
     * @param colQual
     * @return
     */
    public static VersioningModel determineVersioningScheme(final FamilyHB colFam, final QualHB colQual) {
        VersioningModel versioningScheme = null;

        if (colQual != null) {
            versioningScheme = colQual.getVersioning();
        }
        if (versioningScheme == null) {
            if (colFam != null) {
                versioningScheme = colFam.getVersioning();
            }
        }
        return versioningScheme;
    }

    public static VersioningModel determineVersioningScheme(final ColSpecFrozen colSpec) {
        return determineVersioningScheme(colSpec.getFamily(), colSpec.getColumn());
    }

    private static <S> S identifySerializationComponent(final FamilyQualifierPair toUseForCell, final QualHB qualModel, final FamilyHB famModel, final TableHB tableModel, final Function<CellSerializable, S> componentGetter, final BiFunction<FamilyHB, QualHB, S> familyQualSpecificComponentGetter) {
        S component = null;
        if (qualModel != null) {
            component = componentGetter.apply(qualModel);
        }
        if ((component == null) && (famModel != null)) {
            if (toUseForCell != null) {
                component =
                    familyQualSpecificComponentGetter.apply(famModel, toUseForCell.getColumn());
            }
            if (component == null) {
                component = componentGetter.apply(famModel);
            }
        }
        if ((component == null) && (tableModel != null)) {
            component = componentGetter.apply(tableModel);
        }
        return component;
    }

    /**
     * Identifies the deserializer to be used for a given cell, based on a given combination of the
     * models for column qualifier, column family, and table. The method for selecting the
     * deserializer is as follows, in descending order of priority (the logic proceeds from first
     * to last until a non-null deserializer is selected, or all possibilities are exhausted):
     * <ol>
     *     <li><strong>Column Qualifier Model:</strong> If the column qualifier model
     *     (<code>qualModel</code>) is provided and if it specifies a deserializer, then use it.
     *     </li>
     *     <li><strong>Column Family Model (for specific qualifier):</strong> If the column family
     *     model (<code>famModel</code>) is provided and if it specifies a deserializer intended to
     *     <em>apply to a particular column qualifier</em> which matches the <em>actual</em> column
     *     qualifier for the cell in question (from <code>toUseForCell</code>), then use said
     *     qualifier in combination with the column family model to find the deserializer. (Note
     *     that, depending on the structure of the models, and in particular whether qualifier-based
     *     versioning is in use, the column qualifier of the actual cell may not exactly match the
     *     column qualifier model -- hence the need to provide the actual cell FamilyQualifierPair
     *     (<code>toUseForCell</code>) to this method.</li>
     *     <li><strong>Column Family Model:</strong> If the column family model
     *     (<code>famModel</code>) is provided and if it specifies a <em>default</em> deserializer
     *     to use in the event that no qualifier-specific deserializer can be found, then use that
     *     deserializer.</li>
     *     <li><strong>Table Model:</strong> If the table model (<code>tableModel</code>) is
     *     provided and if it specifies a deserializer, then use it.</li>
     * </ol>
     * @param toUseForCell FamilyQualifierPair representing the concrete column family and qualifier
     *                     of a given cell, if available. Note that the qualifier defined in this
     *                     pair may differ from the qualifier model provided, for example if the
     *                     QualModel specifies a range of qualifiers, or if it indicates the use of
     *                     qualifier-based versioning (which would mutate the concrete form of the
     *                     qualifier value).
     * @param qualModel QualModel representing the column qualifier model pertinent to a value for
     *                  which the deserializer must be identified.
     * @param famModel FamilyModel representing the column family model pertinent to a value for
     *                 which the deserializer must be identified.
     * @param tableModel TableModel representing the table model pertinent to a value for which the
     *                   deserialier must be identified.
     * @return CellDeserializer identified according to the priority logic indicated above, or null
     * if no CellDeserializer could be found.
     */
    public static CellDeserializer identifyDeserializer(final FamilyQualifierPair toUseForCell, final QualHB qualModel, final FamilyHB famModel, final TableHB tableModel) {
        return identifySerializationComponent(toUseForCell,
                                              qualModel,
                                              famModel,
                                              tableModel,
                                              CellSerializable::getDeserializer,
                                              FamilyHB::getDeserializer);
    }

    /**
     * Equivalent to {@link #identifyDeserializer(null, QualHB, FamilyHB, TableHB)}
     * @see #identifyDeserializer(FamilyQualifierPair, QualHB, FamilyHB, TableHB)
     */
    public static CellDeserializer identifyDeserializer(final QualHB qualModel, final FamilyHB famModel, final TableHB tableModel) {
        return identifyDeserializer(null, qualModel, famModel, tableModel);
    }

    /**
     * Identifies the serializer to be used for a given cell, based on a given combination of the
     * models for column qualifier, column family, and table. The method for selecting the
     * serializer is as follows, in descending order of priority (the logic proceeds from first
     * to last until a non-null serializer is selected, or all possibilities are exhausted):
     * <ol>
     *     <li><strong>Column Qualifier Model:</strong> If the column qualifier model
     *     (<code>qualModel</code>) is provided and if it specifies a serializer, then use it.
     *     </li>
     *     <li><strong>Column Family Model (for specific qualifier):</strong> If the column family
     *     model (<code>famModel</code>) is provided and if it specifies a serializer intended to
     *     <em>apply to a particular column qualifier</em> which matches the <em>actual</em> column
     *     qualifier for the cell in question (from <code>toUseForCell</code>), then use said
     *     qualifier in combination with the column family model to find the serializer. (Note
     *     that, depending on the structure of the models, and in particular whether qualifier-based
     *     versioning is in use, the column qualifier of the actual cell may not exactly match the
     *     column qualifier model -- hence the need to provide the actual cell FamilyQualifierPair
     *     (<code>toUseForCell</code>) to this method.</li>
     *     <li><strong>Column Family Model:</strong> If the column family model
     *     (<code>famModel</code>) is provided and if it specifies a <em>default</em> serializer
     *     to use in the event that no qualifier-specific serializer can be found, then use that
     *     serializer.</li>
     *     <li><strong>Table Model:</strong> If the table model (<code>tableModel</code>) is
     *     provided and if it specifies a serializer, then use it.</li>
     * </ol>
     * @param toUseForCell FamilyQualifierPair representing the concrete column family and qualifier
     *                     of a given cell, if available. Note that the qualifier defined in this
     *                     pair may differ from the qualifier model provided, for example if the
     *                     QualModel specifies a range of qualifiers, or if it indicates the use of
     *                     qualifier-based versioning (which would mutate the concrete form of the
     *                     qualifier value).
     * @param qualModel QualModel representing the column qualifier model pertinent to a value for
     *                  which the serializer must be identified.
     * @param famModel FamilyModel representing the column family model pertinent to a value for
     *                 which the serializer must be identified.
     * @param tableModel TableModel representing the table model pertinent to a value for which the
     *                   serialier must be identified.
     * @return CellSerializer identified according to the priority logic indicated above, or null
     * if no CellSerializer could be found.
     */
    public static CellSerializer identifySerializer(final FamilyQualifierPair toUseForCell, final QualHB qualModel, final FamilyHB famModel, final TableHB tableModel) {
        return identifySerializationComponent(toUseForCell,
                                              qualModel,
                                              famModel,
                                              tableModel,
                                              CellSerializable::getSerializer,
                                              FamilyHB::getSerializer);
    }

    /**
     * Equivalent to {@link #identifySerializer(null, QualHB, FamilyHB, TableHB)}
     * @see #identifySerializer(FamilyQualifierPair, QualHB, FamilyHB, TableHB)
     */
    public static CellSerializer identifySerializer(final QualHB qualModel, final FamilyHB famModel, final TableHB tableModel) {
        return identifySerializer(null, qualModel, famModel, tableModel);
    }

    private SpecUtil() {}
}
