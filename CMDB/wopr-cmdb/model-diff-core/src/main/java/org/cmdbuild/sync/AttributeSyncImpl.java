/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.modeldiff.diff.schema.CmClasseSchemaNode;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class AttributeSyncImpl implements AttributeSync {

    private static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    private final UserClassService classService;
    private final AttributeTypeConversionService attributeSerializer;
    private final DaoService dao;

    public AttributeSyncImpl(UserClassService classService,
            AttributeTypeConversionService attributeSerializer,
            DaoService dao) {
        this.classService = checkNotNull(classService);
        this.attributeSerializer = checkNotNull(attributeSerializer);
        this.dao = checkNotNull(dao);
    }

    @Override
    public Attribute readAttribute(String classId, String attrId) {
        return classService.getUserClass(classId).getAttribute(attrId);
    }

    // @todo AFE TBC
//    @Override
//    public AttributeImplBuilder buildAttribute(String name, CardAttributeType type, Classe owner) {
//        return AttributeImpl.builder().withName(name).withType(type).withOwner(owner);
//    }

    @Override
    public Attribute build(String attribName, Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition(ownerEntryType);
    }

    @Override
    public Attribute build_toDeactivated(String attribName, Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType) {
        Map<String, Object> deactivatedAttribCmdbSerialization = CmClasseSchemaNode.deactivate(attribCmdbSerialization);
        WsAttributeData deactivatedAttribData = buildAttributeData(deactivatedAttribCmdbSerialization);

        return deactivatedAttribData.toAttrDefinition(ownerEntryType);
    }

    // @todo AFE TBC
//    @Override
//    public void remove(Attribute attribute) {
//        // @todo AFE TBC as in UserClassServiceImpl.deleteAttribute()
//        // checkArgument(attribute.hasServiceModifyPermission(), "CM: permission denied: user not authorized to delete attribute = %s", attribute);
//        dao.deleteAttribute(attribute);
//    }
    @Override
    public CmMapUtils.FluentMap<String, Object> serializeAttributeProps(Attribute curAttrib) {
        return attributeSerializer.serializeAttributeType(curAttrib, false);
    }

    @Override
    public Attribute add(Attribute attribute) {
        return classService.createAttribute(attribute);
    }

    @Override
    public Attribute update(Attribute attribute) {
        return classService.updateAttribute(attribute);
    }

    /**
     * {@link Attribute} <i>metadata</i> is is exploded in CMDBuild
     * serialization and needs to be build again before <i>comparing</i>.
     *
     * @param attribCmdbSerialization
     * @return
     */
    @Override
    public Map<String, String> buildMetadata(Map<String, Object> attribCmdbSerialization) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition().getMetadata().getAll();
    }

    /**
     * As done in {@link ClassAttributeWs}.
     *
     * @param attribCmdbSerialization
     * @param ownerEntryType
     * @return
     */
    // @todo AFE TBC
    private Attribute buildAttributeFor(Map<String, Object> attribCmdbSerialization, EntryType ownerEntryType) {
        WsAttributeData attribData = buildAttributeData(attribCmdbSerialization);

        return attribData.toAttrDefinition(ownerEntryType);
    }

    /**
     * {@link Attribute} <i>metadata</i> is exploded in CMDBuild serialization
     * and needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param attribCmdbSerialization
     * @return
     */
    public WsAttributeData buildAttributeData(Map<String, Object> attribCmdbSerialization) {
        // Reconstructs metadata from Attribute serialization        
        return getSystemObjectMapper().convertValue(attribCmdbSerialization, WsAttributeData.class);
    }

    private ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }
}
