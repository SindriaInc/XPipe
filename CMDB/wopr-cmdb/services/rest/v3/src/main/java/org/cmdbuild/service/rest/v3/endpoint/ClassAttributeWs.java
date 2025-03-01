package org.cmdbuild.service.rest.v3.endpoint;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.Collections.emptyMap;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/attributes/")
@Produces(APPLICATION_JSON)

public class ClassAttributeWs {

    private final UserClassService classService;
    private final AttributeTypeConversionService conversionService;

    public ClassAttributeWs(UserClassService classService, AttributeTypeConversionService conversionService) {
        this.classService = checkNotNull(classService);
        this.conversionService = checkNotNull(conversionService);
    }

    @GET
    @Path("{attrId}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        Attribute attribute = classService.getUserAttribute(classId, attrId);
        return response(conversionService.serializeAttributeType(attribute));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<Attribute> attributeList = isAdminViewMode(viewMode) ? classService.getUserAttributes(classId) : classService.getActiveUserAttributes(classId);
        List list = attributeList.stream().sorted((a, b) -> Integer.compare(a.getIndex(), b.getIndex())).map(a -> conversionService.serializeAttributeType(a, isAdminViewMode(viewMode))).collect(toList());
        return response(paged(list, offset, limit));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
        return response(conversionService.serializeAttributeType(attribute));
    }

    @PUT
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
        return response(conversionService.serializeAttributeType(attribute));
    }

    @DELETE
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        classService.deleteAttribute(classId, attrId);
        return success();
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object reorder(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Classe classe = classService.getUserClass(classId);
        classService.updateAttributes(prepareAttributesToUpdateForOrder(classe::getAttribute, attrOrder));
        classe = classService.getUserClass(classId);
        return response(attrOrder.stream().map(classe::getAttribute).map(conversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }

    public static List<Attribute> prepareAttributesToUpdateForOrder(Function<String, Attribute> attributeFun, List<String> attributes) {
        checkArgument(set(attributes).size() == attributes.size());
        List<Attribute> list = list();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = checkNotNull(attributeFun.apply(attributes.get(i)));
            int newIndex = i + 1;
            if (attribute.getIndex() != newIndex) {
                list.add(AttributeImpl.copyOf(attribute).withIndex(newIndex).build());
            }
        }
        return list;
    }

}
