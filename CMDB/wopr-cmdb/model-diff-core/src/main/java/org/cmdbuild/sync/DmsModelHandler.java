/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassImpl;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

// @todo AFE TBC unificare con org.cmdbuild.model.dataset.data.DmsModelHandler?
/**
 * Contains data for:
 * <ol>
 * <li><i>dms model</i> (a special type of {@link Classe}) stuff;
 * <li><i>dms category</i> (a special type of {@link LookupType}) stuff.
 * </ol>
 *
 * @author afelice
 */
public class DmsModelHandler {

    protected static final String ATTRIBUTE_SERIALIZATION_DMS_CATEGORY_FIELDNAME = "dmsCategory";
    protected static final String ATTRIBUTE_SERIALIZATION_DMS_MODEL_FIELDNAME = "dmsModel";

    private final String defaultDmsCategoryName;

    private final DmsSync dmsSync;
    private final UserClassService classService;

    /**
     * Filled by {@link SchemaCollector}.
     */
    private final Map<String, Classe> internalDmsModelColl = map();

    /**
     * Filled by {@link SchemaCollector}.
     */
    private final Map<String, ExtendedClass> dmsModelColl = map();


    /**
     * <dl>
     * <dt>key
     * <dd><i>dms category</i> {@link LookupType} name;
     * <dt>value
     * <dd>all <i>dms category</i> {@link LookupValue} values.
     * </dl>
     */
    private final Map<String, List<LookupValue>> allCategoriesValues = map();

    public DmsModelHandler(DmsSync dmsSync, UserClassService classService) {
        this.dmsSync = dmsSync;
        this.defaultDmsCategoryName = dmsSync.readDefaultDmsCategory();
        this.classService = classService;
    }

    /**
     * For <code>FILE</code> attributes, add related DMS related <i>dms
     * category</i> information.
     *
     * <p>
     * As in {@link ModelCollectorImpl#addAttributeDmsStuff()}.
     *
     * @param classe
     * @param curAttrib
     * @param attribSerialization
     */
    public void addAttributeDmsStuff(Classe classe, Attribute curAttrib,
            Map<String, Object> attribSerialization) {
        if (curAttrib.isOfType(AttributeTypeName.FILE)) {
            // Serialization (see AttributeTypeCopnversionServie.serializeAttributeSpecificValues()) has values:
            // - dmsCategory --> the related lookup value name;
            // - dmsModel --> the related dmsModel Classe;
            Object dmsModelObj = attribSerialization.get(ATTRIBUTE_SERIALIZATION_DMS_MODEL_FIELDNAME);
            if (dmsModelObj != null) {
                Pair<String, List<LookupValue>> allDmsCategoryValues = dmsSync.readDmsCategoryValues(classe, defaultDmsCategoryName);

                // Add DMS data
                retrieveDmsModel((String) dmsModelObj, allDmsCategoryValues.getKey(), allDmsCategoryValues.getValue());
            }
        }
    }

    public String getDefaultDmsCategory() {
        return defaultDmsCategoryName;
    }

    public List<ExtendedClass> getDmsModels() {
        return list(dmsModelColl.values());
    }

    /**
     *
     * @return <dl>
     * <dt>key
     * <dd><i>dms category</i> {@link LookupType} name;
     * <dt>value
     * <dd>all <i>dms category</i> {@link LookupValue} values.
     * </dl>
     */
    public Map<String, List<LookupValue>> getDmsCategoriesLookups() {
        return allCategoriesValues;
    }

    public void addLookupValues(String dmsCategoryType, List<LookupValue> allCategoryValues) {
        // @todo AFE aggiungere in dmsCategoryNodes
        allCategoriesValues.put(dmsCategoryType, allCategoryValues);
    }

    private void retrieveDmsModel(String dmsModelName,
            String dmsCategoryType, List<LookupValue> allCategoryValues) {
        if (dmsModelColl.containsKey(dmsModelName)) {
            return;
        }

        internalDmsModelColl.computeIfAbsent(dmsModelName,
                classeName -> {
                    return classService.getUserClass(classeName);
                });
        Classe dmsModel = internalDmsModelColl.get(dmsModelName);
        addLookupValues(dmsCategoryType, allCategoryValues);
        ExtendedClass extendedDmsModel = buildExtendedClass(dmsModel);

        // @todo AFE aggiungere in dmsModelNodes
        dmsModelColl.put(dmsModelName, extendedDmsModel);
    }

    private ExtendedClass buildExtendedClass(Classe dmsModel) {
        ExtendedClass extendedDmsModel = ExtendedClassImpl.builder()
                .withClasse(dmsModel)
                .withLookupValuesByAttr(allCategoriesValues)
                .withFormTriggers(emptyList())
                .withContextMenuItems(emptyList())
                .withWidgets(emptyList())
                .withCalendarTriggers(emptyList())
                .build();
        return extendedDmsModel;
    }

}
