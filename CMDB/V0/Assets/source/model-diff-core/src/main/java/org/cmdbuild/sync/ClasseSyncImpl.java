/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;


import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_LOOKUP_VALUES;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: can't be all based on ids, because if diffing different systems
 * ids won't match.
 *
 * @author afelice
 */
@Component
public class ClasseSyncImpl implements ClasseSync {

    private static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    private final UserClassService classService;
    private final ClassSerializationHelper classHelper;
    private final ClasseRepository classeRepository;

    public ClasseSyncImpl(UserClassService classService, ClassSerializationHelper classHelper, ClasseRepository classeRepository) {
        this.classService = checkNotNull(classService);
        this.classHelper = checkNotNull(classHelper);
        this.classeRepository = checkNotNull(classeRepository);
    }

    /**
     *
     * @param classeName
     * @return
     */
    @Override
    public Classe read(String classeName) {
        return classService.getUserClass(classeName);
    }

    @Override
    public ExtendedClass readExtended(String classeName) {
        return classService.getExtendedClass(classeName, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
    }

    @Override
    public ExtendedClass readExtended(Classe classe) {
        return classService.getExtendedClass(classe, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
    }

    @Override
    public List<Classe> readAll() {
        return classService.getAllUserClasses();
    }

    @Override
    // @todo AFE TBC
    public List<Classe> readAll(boolean includeInactiveElements, boolean includeLookupValues, String filterStr) {
        // @todo AFE come in Class.readAll()
//        List list = (isAdminViewMode(viewMode) ? dao.getAllClasses().stream() : dao.getAllClasses().stream().filter(Classe::isActive)).filter(Classe::isDmsModel)
//                .map(detailed ? compose(helper::buildFullDetailExtendedResponse, classService::getExtendedClass) : helper::buildBasicResponse).collect(toList());
//
//        //TODO duplicate code with class ws, improve this
//        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
//        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
//        if (filter.hasAttributeFilter()) {
//            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
//        }
        return list();
    }

    @Override
    public ExtendedClass add(String classeName, Map<String, Object> classeCmdbSerialization) {
        WsClassData classeData = buildClasseData(classeCmdbSerialization);
        // See ClassWs.create()
        return classService.createClass(classHelper.extendedClassDefinitionForNewClass(classeData));
    }

    @Override
    public ExtendedClass update(String classeName, Map<String, Object> classeCmdbSerialization) {
        WsClassData classeData = buildClasseData(classeCmdbSerialization);
        return update(classeName, classeData);
    }

    @Override
    public ExtendedClass deactivate(Classe classe) {
        Classe deactivatedClasse = ClasseImpl.copyOf(classe)
                .withMetadata(
                        ClassMetadataImpl.copyOf(classe.getMetadata())
                                .withActive(false)
                                .build()
                ).build();
        return update(classe.getName(), buildClasseData(deactivatedClasse));
    }

    @Override
    public void remove(Classe classe) {
        // @todo AFE as in UserClasseService.deleteClass()
        // checkArgument(classe.hasServiceModifyPermission(), "CM: permission denied: user not authorized to drop class");
        // TBC:
//        formTriggerService.deleteForClass(classe);
//        contextMenuService.deleteForClass(classe);
//        widgetService.deleteForClass(classe);
        classeRepository.deleteClass(classe);
    }

    @Override
    public CmMapUtils.FluentMap<String, Object> serializeClasseProps(ExtendedClass extendedClass) {
        return classHelper.buildFullDetailExtendedResponse(extendedClass);
    }

    private ExtendedClass update(String classeName, WsClassData classeData) {
        // See ClassWs.update()
        return classService.updateClass(classHelper.extendedClassDefinitionForExistingClass(classeName, classeData));
    }

    private ClassSerializationHelper.WsClassData buildClasseData(Classe classe) {
        return buildClasseData(classHelper.buildFullDetailResponse(classe));
    }

    /**
     * {@link Classe} <i>metadata</i> is exploded in CMDBuild serialization and
     * needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param classeCmdbSerialization
     * @return
     */
    private WsClassData buildClasseData(Map<String, Object> classeCmdbSerialization) {
        // Reconstructs metadata from Classe serialization        
        return getSystemObjectMapper().convertValue(classeCmdbSerialization, ClassSerializationHelper.WsClassData.class);
    }

    private ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }

}
