/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ACTIVE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_INHERITED_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_REFERENCED_CLASSE_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_TYPE_SERIALIZATION;
import org.cmdbuild.modeldiff.diff.CmDifferRepository;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaItemDeltaBuilder.buildSchemaItemAttributesDataNode_Unique;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.sync.ClasseSync;
import org.cmdbuild.sync.DmsModelHandler;
import org.cmdbuild.sync.LookupHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * <b>Concrete placeholder</b> for visitable (to engage polymorphism and choice
 * of visitor in related repository), wrap for a real <i>schema</i>
 * {@link Classe} stuff, composed of {@link Attribute} items.
 *
 * <p>
 * Knows how to build a <i>schema node</i> for a {@link Classe} and its
 * contained {@link Attribute}s.
 *
 * @author afelice
 */
public class CmClasseSchemaNode extends CmSchemaItemDataNode {

    public CmClasseSchemaNode(CmSchemaItemAttributesData itemData) {
        super(itemData);
    }

    @Override
    public CmDeltaList calculateDiff(CmDifferRepository differRepository, CmSchemaItemDataNode rightModelNode) {
        return super.calculateDiff(differRepository, rightModelNode);
    }

    /**
     *
     * @return name of parent; <code>null</code> if none.
     */
    public String getParent() {
        return toStringOrNull(getModelObj().getAttributesSerialization().get(ATTR_PARENT_SERIALIZATION));
    }

    public CmSchemaItemAttributesDataNode getAttributeNode(String attribName) {
        return getComponents().stream().filter(aNode -> aNode.getModelObj().getName().equals(attribName)).findFirst().get();
    }

    /**
     * Adds to the node:
     * <ol>
     * <li>the serialization for the {@link Classe};
     * <li>for each contained {@link Attribute}, a
     * {@link CmSchemaItemAttributesDataNode} with attribute's serialization;
     * </ol>
     *
     * @param aClasse
     * @param lookupDataHandler
     * @param dmsDataHandler
     * @param classeSync
     * @param attributeSerializer
     * @return
     */
    static public CmClasseSchemaNode from(Classe aClasse,
            LookupHandler lookupDataHandler, DmsModelHandler dmsDataHandler,
            ClasseSync classeSync, AttributeTypeConversionService attributeSerializer) {

        ExtendedClass extendedClass = classeSync.readExtended(aClasse.getName());
        Map<String, Object> classeSerialization = classeSync.serializeClasseProps(extendedClass);

        // Store class serialization in a CmSchemaItemAttributesDataNode
        CmClasseSchemaNode result = new CmClasseSchemaNode(new CmSchemaItemAttributesData(aClasse.getName(), classeSerialization));

        // Add attributes as components
        aClasse.getAllAttributes().stream().
                map(curAttrib -> buildSchemaItemAttributesDataNode_Unique(aClasse.getName(), curAttrib.getName(), attributeSerializer.serializeAttributeType(curAttrib, true)))
                .forEach(curAttribNode -> result.addComponent(curAttribNode));

        // @todo AFE add lookups
        return result;
    }

    static public String getName(Map<String, Object> props) {
        return (String) props.get(ATTR_NAME_SERIALIZATION);
    }

    static public String getParent(Map<String, Object> props) {
        return (String) props.get(ATTR_PARENT_SERIALIZATION);
    }

    static public String getAttributeType(Map<String, Object> props) {
        return (String) props.get(ATTR_TYPE_SERIALIZATION);
    }

    static public boolean isInherited(CmSchemaItemAttributesDataNode itemAttribNode) {
        return isInherited(itemAttribNode.getModelObj().getAttributesSerialization());
    }

    static public boolean isInherited(Map<String, Object> itemAttribCmdbSerialization) {
        return Boolean.TRUE.equals(itemAttribCmdbSerialization.get(ATTR_INHERITED_SERIALIZATION));
    }

    static public boolean isActive(Map<String, Object> itemAttribCmdbSerialization) {
        return Boolean.TRUE.equals(itemAttribCmdbSerialization.get(ATTR_ACTIVE_SERIALIZATION));
    }

    static public Map<String, Object> deactivate(Map<String, Object> itemAttribCmdbSerialization) {
        return map(itemAttribCmdbSerialization).with(
                ATTR_ACTIVE_SERIALIZATION, false
        );
    }

    /**
     * Used when <i>removing</i> a {@link Classe}: all
     * <i>references</i>, <i>foreign keys</i> to that has to be deactivated.
     *
     * @param referredClasseNames
     * @return
     */
    public List<CmSchemaItemAttributesDataNode> getRelations(Set<String> referredClasseNames) {
        return getComponents().stream().filter(attribDataNode -> isReferenceTo(attribDataNode, referredClasseNames)).collect(toList());
    }

    public static String getReferencedType(CmSchemaItemAttributesDataNode attribDataNode) {
        return getReferencedType(attribDataNode.getModelObj().getAttributesSerialization());
    }

    /**
     * As {@link AttributeTypeName} is serialized in {@link AttributeTypeConversionService#serializeAttributeType(org.cmdbuild.dao.entrytype.Attribute)
     * }.
     */
    private final static Set<String> RELATION_TYPES = set(
            AttributeTypeConversionService.serializeAttributeType(REFERENCE),
            AttributeTypeConversionService.serializeAttributeType(FOREIGNKEY)
    );

    private boolean isReferenceTo(CmSchemaItemAttributesDataNode attribDataNode, Set<String> referredClasseNames) {
        Map<String, Object> attribCmdbSerialization = attribDataNode.getModelObj().getAttributesSerialization();
        String attributeTypeStr = getAttributeType(attribCmdbSerialization);

        return RELATION_TYPES.contains(attributeTypeStr) && referredClasseNames.contains(getReferencedType(attribCmdbSerialization));
    }

    private static String getReferencedType(Map<String, Object> props) {
        return (String) props.get(ATTR_REFERENCED_CLASSE_SERIALIZATION);
    }
}
