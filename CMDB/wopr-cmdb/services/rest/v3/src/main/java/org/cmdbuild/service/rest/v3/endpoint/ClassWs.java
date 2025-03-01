package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_DEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_LOOKUP_VALUES;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_LOOKUP_VALUES;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("{a:classes}/")
@Produces(APPLICATION_JSON)
public class ClassWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserClassService classService;
    private final ClassSerializationHelper helper;

    public ClassWs(UserClassService classService, ClassSerializationHelper helper) {
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed, @QueryParam("includeLookupValues") @DefaultValue(FALSE) Boolean includeLookupValues, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr) {
        List list = (isAdminViewMode(viewMode) ? classService.getAllUserClasses() : classService.getActiveUserClasses()).stream()
                .map(detailed ? compose(helper::buildFullDetailExtendedResponse, c -> isAdminViewMode(viewMode) ? classService.getExtendedClass(c, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null) : classService.getExtendedClass(c, CQ_FOR_USER, CQ_FILTER_DEVICE, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null)) : helper::buildBasicResponse).collect(toList());
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
        }
        return response(paged(list, offset, limit));
    }

    @GET
    @Path("{classId}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, @QueryParam("includeLookupValues") @DefaultValue(FALSE) Boolean includeLookupValues) {
        ExtendedClass classe = isAdminViewMode(viewMode) ? classService.getExtendedClass(classId, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null) : classService.getExtendedClass(classId, CQ_FOR_USER, CQ_FILTER_DEVICE, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null);
        return buildResponse(classe);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(WsClassData data) {
        logger.debug("create classe with data = {}", data);
        ExtendedClass classe = classService.createClass(helper.extendedClassDefinitionForNewClass(data));
        return buildResponse(classe);
    }

    @PUT
    @Path("{classId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(@PathParam("classId") String classId, WsClassData data) {
        logger.debug("update classe = {} with data = {}", classId, data);
        ExtendedClass classe = classService.updateClass(helper.extendedClassDefinitionForExistingClass(classId, data));
        return buildResponse(classe);
    }

    @DELETE
    @Path("{classId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(@PathParam("classId") String classId) {
        classService.deleteClass(classId);
        return success();
    }

    private Object buildResponse(ExtendedClass classe) {
        return response(helper.buildFullDetailExtendedResponse(classe));
    }

}
