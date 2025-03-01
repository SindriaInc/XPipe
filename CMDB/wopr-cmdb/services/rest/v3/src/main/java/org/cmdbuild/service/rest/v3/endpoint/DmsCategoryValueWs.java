package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
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
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.helpers.LookupValueWsCommons;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;

@Path("dms/categories/{" + LOOKUP_TYPE_ID + "}/values/")
@Produces(APPLICATION_JSON)
public class DmsCategoryValueWs extends LookupValueWsCommons {

    public DmsCategoryValueWs(LookupService lookupLogic, ObjectTranslationService translationService) {
        super(lookupLogic, translationService);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    public Object read(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam(LOOKUP_VALUE_ID) Long lookupValueId) {
        return doRead(lookupTypeId, lookupValueId);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        if (equal(lookupTypeId, "_ALL")) {
            return response(paged(lookupService.getAllTypes(filterStr).stream().filter(LookupType::isDmsCategorySpeciality).map(LookupType::getName).sorted()
                    .flatMap(t -> isAdminViewMode(viewMode) ? lookupService.getAllLookup(t, null, null, parseFilter(filterStr)).stream() : lookupService.getActiveLookup(t, null, null, parseFilter(filterStr)).stream())
                    .map(this::toResponse).collect(toImmutableList()), offset, limit));
        } else {
            return doReadAll(lookupTypeId, limit, offset, filterStr, viewMode);
        }
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object create(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, WsLookupValue wsLookupValue) {
        checkIsDmsCategory(lookupTypeId);
        return doCreate(lookupTypeId, wsLookupValue);
    }

    @PUT
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object update(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId, WsLookupValue wsLookupValue) {
        checkIsDmsCategory(lookupTypeId);
        return doUpdate(lookupTypeId, lookupId, wsLookupValue);
    }

    @DELETE
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object delete(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, @PathParam("lookupValueId") Long lookupId) {
        checkIsDmsCategory(lookupTypeId);
        return doDelete(lookupTypeId, lookupId);
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object reorder(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, List<Long> lookupValueIds, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        return doReorder(lookupTypeId, lookupValueIds, viewMode);
    }

    private void checkIsDmsCategory(String lookupTypeId) {//TODO duplicate code
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        checkArgument(lookupType.isDmsCategorySpeciality(), "invalid lookup type =< %s > : not a dms category", lookupTypeId);
    }
}
