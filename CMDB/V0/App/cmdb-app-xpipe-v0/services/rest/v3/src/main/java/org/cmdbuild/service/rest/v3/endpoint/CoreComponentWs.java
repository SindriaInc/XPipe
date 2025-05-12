package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.HeaderParam;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentImpl;
import org.cmdbuild.corecomponents.CoreComponentImpl.CoreComponentImplBuilder;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.corecomponents.CoreComponentType;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

@Path("components/core/{type}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CoreComponentWs {

    private final CoreComponentService service;

    public CoreComponentWs(CoreComponentService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CORECOMPONENTS_VIEW_AUTHORITY)
    public Object listByType(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("type") CoreComponentType type, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        return response(list(isAdminViewMode(viewMode) ? service.getComponentsByType(type) : service.getActiveComponentsByType(type)).map(detailed ? this::serializeDetails : this::serializeInfo));
    }

    @GET
    @Path("{code}")
    public Object get(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("code") String code) {
        return response(serializeDetails(isAdminViewMode(viewMode) ? service.getComponent(code) : service.getActiveComponent(code)));
    }

    @DELETE
    @Path("{code}")
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("code") String code) {
        service.deleteComponent(code);
        return success();
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object create(@PathParam("type") CoreComponentType type, WsCoreComponentData data) {
        return response(serializeDetails(service.createComponent(data.toCoreComponent().withType(type).build())));
    }

    @PUT
    @Path("{code}")
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object update(@PathParam("code") String code, @PathParam("type") CoreComponentType type, WsCoreComponentData data) {
        CoreComponent component = service.getComponent(code);
        component = data.toCoreComponent().withType(component.getType()).withCode(component.getCode()).build();
        return response(serializeDetails(service.updateComponent(component)));
    }

    private FluentMap serializeInfo(CoreComponent component) {
        return map(
                "_id", component.getCode(),
                "active", component.isActive(),
                "name", component.getCode(),
                "description", component.getDescription(),
                "type", serializeEnum(component.getType()));
    }

    private Object serializeDetails(CoreComponent component) {
        return serializeInfo(component).with("data", component.getData());
    }

    public static class WsCoreComponentData {

        private final String description, data, code;
        private final Boolean isActive;

        public WsCoreComponentData(@JsonProperty("name") String code,
                @JsonProperty("description") String description,
                @JsonProperty("data") String data,
                @JsonProperty("active") Boolean isActive) {
            this.description = description;
            this.code = code;
            this.data = data;
            this.isActive = isActive;
        }

        public CoreComponentImplBuilder toCoreComponent() {
            return CoreComponentImpl.builder()
                    .withActive(isActive)
                    .withCode(code)
                    .withDescription(description)
                    .withData(data);
        }
    }
}
