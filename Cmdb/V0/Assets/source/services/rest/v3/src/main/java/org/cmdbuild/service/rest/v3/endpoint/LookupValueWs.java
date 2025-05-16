package org.cmdbuild.service.rest.v3.endpoint;

import java.util.List;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_TYPE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_VALUE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import org.cmdbuild.translation.ObjectTranslationService;
import jakarta.annotation.security.RolesAllowed;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.helpers.LookupValueWsCommons;

@Path("lookup_types/{" + LOOKUP_TYPE_ID + "}/values/")
@Produces(APPLICATION_JSON)
public class LookupValueWs extends LookupValueWsCommons {

    public LookupValueWs(LookupService lookupLogic, ObjectTranslationService translationService) {
        super(lookupLogic, translationService);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    public Object read(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam(LOOKUP_VALUE_ID) Long lookupValueId) {
        return doRead(lookupTypeId, lookupValueId);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @QueryParam("forClass") String forClass, @QueryParam("forAttr") String forAttr, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        if (isNotBlank(forClass) && isNotBlank(forAttr)) {
            return doReadDistinct(lookupTypeId, limit, offset, viewMode, forClass, forAttr);
        }
        return doReadAll(lookupTypeId, limit, offset, filterStr, viewMode);
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object create(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, WsLookupValue wsLookupValue) {
        return doCreate(lookupTypeId, wsLookupValue);
    }

    @PUT
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object update(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId, WsLookupValue wsLookupValue) {
        return doUpdate(lookupTypeId, lookupId, wsLookupValue);
    }

    @DELETE
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object delete(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId) {
        return doDelete(lookupTypeId, lookupId);
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object reorder(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, List<Long> lookupValueIds, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        return doReorder(lookupTypeId, lookupValueIds, viewMode);
    }

}
