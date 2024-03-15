package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_TYPE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import javax.annotation.security.RolesAllowed;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import org.cmdbuild.service.rest.v3.endpoint.LookupTypeWs.WsLookupType;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("dms/categories/")
@Produces(APPLICATION_JSON)
public class DmsCategoryWs {//TODO improve this, remove duplicate code (?)

    private final LookupService lookupService;

    public DmsCategoryWs(LookupService lookupLogic) {
        this.lookupService = checkNotNull(lookupLogic);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    public Object read(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId) {
        return response(toResponse(lookupService.getLookupType(decodeIfHex(lookupTypeId))));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filter) {
        List<LookupType> lookupTypes = lookupService.getAllTypes(filter).stream().filter(LookupType::isDmsCategorySpeciality).collect(toList());
        return response(paged(lookupTypes, offset, limit).map(this::toResponse));
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object createLookupType(WsLookupType wsLookupType) {
        return response(toResponse(lookupService.createLookupType(wsLookupType.toLookupType(lookupService).withSpeciality(LS_DMSCATEGORY).build())));
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object deleteLookupType(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId) {
        checkIsDmsCategory(lookupTypeId);
        lookupService.deleteLookupType(decodeIfHex(lookupTypeId));
        return success();
    }

    private void checkIsDmsCategory(String lookupTypeId) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        checkArgument(lookupType.isDmsCategorySpeciality(), "invalid lookup type =< %s > : not a dms category", lookupTypeId);
    }

    public Object toResponse(LookupType lookupType) {//TODO duplicate code, fix this
        return map(
                "_id", lookupType.getName(),
                "name", lookupType.getName(),
                "parent", Optional.ofNullable(lookupType.getParent()).map(l -> lookupService.getLookupType(l).getName()).orElse(null),
                "speciality", serializeEnum(lookupType.getSpeciality()),
                "accessType", serializeEnum(lookupType.getAccessType()));
    }
}
