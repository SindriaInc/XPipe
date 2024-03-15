package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
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
import org.cmdbuild.lookup.LookupTypeImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_TYPE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import javax.annotation.security.RolesAllowed;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

@Path("lookup_types/")
@Produces(APPLICATION_JSON)
public class LookupTypeWs {

    private final LookupService lookupService;

    public LookupTypeWs(LookupService lookupLogic) {
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
        List<LookupType> lookupTypes = lookupService.getAllTypes(filter).stream().filter(LookupType::isDefaultSpeciality).collect(toList());
        return response(paged(lookupTypes, offset, limit).map(this::toResponse));
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object createLookupType(WsLookupType wsLookupType) {
        return response(toResponse(lookupService.createLookupType(wsLookupType.toLookupType(lookupService).withSpeciality(LS_DEFAULT).build())));
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object deleteLookupType(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId) {
        lookupService.deleteLookupType(decodeIfHex(lookupTypeId));
        return success();
    }

    public Object toResponse(LookupType lookupType) {
        return map(
                "_id", lookupType.getName(),
                "name", lookupType.getName(),
                "parent", Optional.ofNullable(lookupType.getParent()).map(l -> lookupService.getLookupType(l).getName()).orElse(null),//TODO improve this
                "speciality", serializeEnum(lookupType.getSpeciality()),
                "accessType", serializeEnum(lookupType.getAccessType()));
    }

    public static class WsLookupType {

        public final String name, parent;

        public WsLookupType(@JsonProperty("name") String name, @JsonProperty("parent") @Nullable String parent) {
            this.name = checkNotBlank(name);
            this.parent = emptyToNull(parent);
        }

        public LookupTypeImpl.LookupTypeImplBuilder toLookupType(LookupService lookupService) {//TODO improve this
            return LookupTypeImpl.builder().withName(name).withParent(Optional.ofNullable(emptyToNull(parent)).map(p -> lookupService.getLookupType(p).getId()).orElse(null));//TODO improve this
        }

    }
}
