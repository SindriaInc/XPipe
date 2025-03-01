package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
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
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.service.rest.v3.endpoint.CustomPageWs.parseCustomUiComponentParams;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("components/widget")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class WidgetWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WidgetComponentService service;

    public WidgetWs(WidgetComponentService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object list(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {//TODO fix this, make admin only (??); see ContextMenuComponentWs
        logger.debug("list all widget components for current user");
        List<UiComponentInfo> list = isAdminViewMode(viewMode) ? service.getAll() : service.getActiveForCurrentUserAndDevice();
        return response(list.stream().map(this::serializeInfo));
    }

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") Long id) {
        UiComponentInfo customMenuComponent = service.get(id);
        return toResponse(customMenuComponent);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        service.delete(id);
        return success();
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object deleteForTargetDevice(@PathParam("id") Long id, @PathParam("targetDevice") TargetDevice targetDevice) {
        service.deleteForTargetDevice(id, targetDevice);
        return success();
    }

    @GET
    @Path("{id}/{version}/{file}|{id}/{version}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("id") Long id, @PathParam("version") TargetDevice targetDevice) {
        UiComponentInfo widgetComponent = service.get(id);
        return service.getWidgetData(widgetComponent.getName(), targetDevice);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(@Multipart("-data") List<DataHandler> files, @Multipart(value = "data|DEFAULT", required = false) WsWidgetComponentData data, @QueryParam("merge") @DefaultValue(FALSE) Boolean merge) {
        UiComponentInfo info;
        if (merge) {
            info = service.createOrUpdate(parseCustomUiComponentParams(files));
        } else {
            info = service.create(parseCustomUiComponentParams(files));
        }
        info = UiComponentInfoImpl.copyOf(info).accept(b -> {
            if (data != null) {
                b.withDescription(data.description).withActive(data.isActive);
            }
        }).build();
        return toResponse(service.update(info));
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(@PathParam("id") Long id, @Multipart("-data") List<DataHandler> files, @Multipart(value = "data|DEFAULT", required = false) WsWidgetComponentData data) {
        UiComponentInfo component = service.get(id);
        List<byte[]> versions = parseCustomUiComponentParams(files);
        checkArgument(!versions.isEmpty() || data != null, "missing data");
        if (!versions.isEmpty()) {
            component = service.update(id, versions);
        }
        if (data != null) {
            component = service.update(UiComponentInfoImpl.copyOf(component).withDescription(data.description).withActive(data.isActive).build());
        }
        return toResponse(component);
    }

    private Object toResponse(UiComponentInfo widgetComponent) {
        return response(serializeInfo(widgetComponent));
    }

    private Object serializeInfo(UiComponentInfo widgetComponent) {
        return map(
                "_id", widgetComponent.getId(),
                "active", widgetComponent.isActive(),
                "name", widgetComponent.getName(),
                "description", widgetComponent.getDescription(),
                "alias", widgetComponent.getExtjsAlias(),
                "componentId", widgetComponent.getExtjsComponentId(),
                "devices", CmCollectionUtils.list(widgetComponent.getTargetDevices()).map(CmConvertUtils::serializeEnum));
    }

    public static class WsWidgetComponentData {

        private final String description;
        private final Boolean isActive;

        public WsWidgetComponentData(@JsonProperty("description") String description, @JsonProperty("active") Boolean isActive) {
            this.description = description;
            this.isActive = isActive;
        }

    }
}
