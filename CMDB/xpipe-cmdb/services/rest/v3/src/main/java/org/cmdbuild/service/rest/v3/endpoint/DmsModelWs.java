package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_CEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.endpoint.ClassAttributeWs.WsAttributeData;
import static org.cmdbuild.service.rest.v3.endpoint.ClassAttributeWs.prepareAttributesToUpdateForOrder;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("dms/models/")
@Produces(APPLICATION_JSON)
public class DmsModelWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final UserClassService classService;
    private final ClassSerializationHelper helper;
    private final AttributeTypeConversionService conversionService;
    private final SysReportService reportService;

    public DmsModelWs(DaoService dao, UserClassService classService, ClassSerializationHelper helper, AttributeTypeConversionService conversionService, SysReportService reportService) {
        this.dao = checkNotNull(dao);
        this.classService = checkNotNull(classService);
        this.helper = checkNotNull(helper);
        this.conversionService = checkNotNull(conversionService);
        this.reportService = checkNotNull(reportService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr) {
        List list = (isAdminViewMode(viewMode) ? dao.getAllClasses().stream() : dao.getAllClasses().stream().filter(Classe::isActive)).filter(Classe::isDmsModel)
                .map(detailed ? compose(helper::buildFullDetailExtendedResponse, classService::getExtendedClass) : helper::buildBasicResponse).collect(toList());

        //TODO duplicate code with class ws, improve this
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
        }
        return response(paged(list, offset, limit));
    }

    @GET
    @Path("{classId}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId) {
        Classe classe = getDmsModel(classId);
        return buildResponse(isAdminViewMode(viewMode) ? classService.getExtendedClass(classe, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER) : classService.getExtendedClass(classe, CQ_FOR_USER, CQ_FILTER_CEVICE));//TODO duplicate code with class ws, improve this
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object create(WsClassData data) {
        getDmsModel(data.parentId);
        return buildResponse(classService.createClass(helper.extendedClassDefinitionForNewClass(data)));
    }

    @PUT
    @Path("{classId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object update(@PathParam("classId") String classId, WsClassData data) {
        getDmsModel(classId);
        return buildResponse(classService.updateClass(helper.extendedClassDefinitionForExistingClass(classId, data)));
    }

    @DELETE
    @Path("{classId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("classId") String classId) {
        getDmsModel(classId);
        classService.deleteClass(classId);
        return success();
    }

    @GET
    @Path("{classId}/attributes/{attrId}/")
    public Object readAttribute(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        Attribute attribute = getDmsModel(classId).getAttribute(attrId);
        return response(conversionService.serializeAttributeType(attribute));
    }

    @GET
    @Path("{classId}/attributes/")
    public Object readAllAttributes(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List list = (isAdminViewMode(viewMode) ? getDmsModel(classId).getServiceAttributes() : getDmsModel(classId).getActiveServiceAttributes()).stream().map(conversionService::serializeAttributeType).collect(toList());
        return response(paged(list, offset, limit));
    }

    @POST
    @Path("{classId}/attributes/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object createAttribute(@PathParam(CLASS_ID) String classId, WsAttributeData data) {
        Classe classe = getDmsModel(classId);
        Attribute attribute = classService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
        return response(conversionService.serializeAttributeType(attribute));
    }

    @PUT
    @Path("{classId}/attributes/{attrId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object updateAttributes(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId, WsAttributeData data) {
        Classe classe = getDmsModel(classId);
        Attribute attribute = classService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
        return response(conversionService.serializeAttributeType(attribute));
    }

    @DELETE
    @Path("{classId}/attributes/{attrId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object deleteAttributes(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        getDmsModel(classId);
        classService.deleteAttribute(classId, attrId);
        return success();
    }

    @POST
    @Path("{classId}/attributes/order")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object reorderAttributes(@PathParam(CLASS_ID) String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        classService.updateAttributes(prepareAttributesToUpdateForOrder(getDmsModel(classId)::getAttribute, attrOrder));
        return response(attrOrder.stream().map(getDmsModel(classId)::getAttribute).map(conversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }

    @GET
    @Path("{classId}/print_schema/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printModelSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        return reportService.executeClassSchemaReport(getDmsModel(classId), reportExtFromString(firstNotBlank(extension, FilenameUtils.getExtension(fileName))));
    }

    private Classe getDmsModel(String classId) {
        Classe classe = dao.getClasse(classId);
        checkArgument(classe.isDmsModel(), "invalid class =< %s >: not a dms model", classId);
        return classe;
    }

    private Object buildResponse(ExtendedClass classe) {
        return response(helper.buildFullDetailExtendedResponse(classe));
    }

}
