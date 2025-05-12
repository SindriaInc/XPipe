package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.activation.DataHandler;
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
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import static org.cmdbuild.service.rest.v3.endpoint.CustomPageWs.parseCustomUiComponentParams;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;

@Path("components/contextmenu")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ContextMenuComponentWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ContextMenuComponentService service;

    public ContextMenuComponentWs(ContextMenuComponentService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object list() {
        return response(service.getAll().stream().map(this::serializeInfo));
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
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(@PathParam("id") Long id, @PathParam("targetDevice") TargetDevice targetDevice) {
        UiComponentInfo customMenuComponent = service.get(id);
        return service.getContextMenuData(customMenuComponent.getName(), targetDevice);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(@Multipart("-data") List<DataHandler> files, @QueryParam("merge") @DefaultValue(FALSE) Boolean merge, @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data) {
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
    public Object update(@PathParam("id") Long id, @Multipart("-data") List<DataHandler> files, @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data) {
        UiComponentInfo contextMenuComponent = service.get(id);
        List<byte[]> versions = parseCustomUiComponentParams(files);
        checkArgument(!versions.isEmpty() || data != null, "missing data");
        if (!versions.isEmpty()) {
            contextMenuComponent = service.update(id, versions);
        }
        if (data != null) {
            contextMenuComponent = service.update(UiComponentInfoImpl.copyOf(contextMenuComponent).withDescription(data.description).withActive(data.isActive).build());
        }
        return toResponse(contextMenuComponent);
    }

    private Object toResponse(UiComponentInfo customPage) {
        return response(serializeInfo(customPage));
    }

    private Object serializeInfo(UiComponentInfo customPage) {
        return map(
                "_id", customPage.getId(),
                "active", customPage.isActive(),
                "name", customPage.getName(),
                "description", customPage.getDescription(),
                "alias", customPage.getExtjsAlias(),
                "componentId", customPage.getExtjsComponentId(),
                "devices", CmCollectionUtils.list(customPage.getTargetDevices()).map(CmConvertUtils::serializeEnum));
    }

    public static class WsContextMenuComponentData {

        private final String description;
        private final Boolean isActive;

        public WsContextMenuComponentData(@JsonProperty("description") String description, @JsonProperty("active") Boolean isActive) {
            this.description = description;
            this.isActive = isActive;
        }

    }
}
