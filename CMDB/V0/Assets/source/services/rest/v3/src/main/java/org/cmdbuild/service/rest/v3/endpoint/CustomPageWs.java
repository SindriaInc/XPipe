package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
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
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.io.CmIoUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("custompages/")
@Produces(APPLICATION_JSON)
public class CustomPageWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomPageService service;
    private final ObjectTranslationService translationService;

    public CustomPageWs(CustomPageService customPageService, ObjectTranslationService translationService) {
        this.service = checkNotNull(customPageService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object list(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode) {
        logger.debug("list all custom pages for current user");
        List<UiComponentInfo> list = isAdminViewMode(viewMode) ? service.getAll() : service.getActiveForCurrentUserAndDevice();
        return response(list.stream().map(this::serializeCustomPage));
    }

    @GET
    @Path("{id}")
    public Object get(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("id") Long id) {
        UiComponentInfo customPage = isAdminViewMode(viewMode) ? service.get(id) : service.getForUser(id);
        return toResponse(customPage);
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
        return toResponse(service.deleteForTargetDevice(id, targetDevice));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(@Multipart("-data") List<DataHandler> files, @Multipart(value = "data", required = false) WsCustomPageData data, @QueryParam("merge") @DefaultValue(FALSE) Boolean merge) {
        UiComponentInfo customPage;
        if (merge) {
            customPage = service.createOrUpdate(parseCustomUiComponentParams(files));
        } else {
            customPage = service.create(parseCustomUiComponentParams(files));
        }
        customPage = UiComponentInfoImpl.copyOf(customPage).accept(b -> {
            if (data != null) {
                b.withDescription(data.description).withActive(data.isActive);
            }
        }).build();
        return toResponse(service.update(customPage));
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(@PathParam("id") Long id, @Multipart("-data") List<DataHandler> files, @Multipart(value = "data|DEFAULT", required = false) WsCustomPageData data) {
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

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public DataHandler download(@PathParam("id") Long id, @PathParam("targetDevice") TargetDevice targetDevice) {
        UiComponentInfo customPage = service.get(id);
        return service.getCustomPageData(customPage.getName(), targetDevice);
    }

    public static List<byte[]> parseCustomUiComponentParams(List<DataHandler> files) {
        return CmCollectionUtils.list(files).without(f -> equal(f.getName(), "data")).map(CmIoUtils::toByteArray);
    }

    private Object toResponse(UiComponentInfo customPage) {
        return response(serializeCustomPage(customPage));
    }

    private Object serializeCustomPage(UiComponentInfo customPage) {
        return map(
                "_id", customPage.getId(),
                "active", customPage.isActive(),
                "name", customPage.getName(),
                "description", customPage.getDescription(),
                "_description_translation", translationService.translateCustomPageDesciption(customPage.getName(), customPage.getDescription()),
                "alias", customPage.getExtjsAlias(),
                "componentId", customPage.getExtjsComponentId(),
                "devices", CmCollectionUtils.list(customPage.getTargetDevices()).map(CmConvertUtils::serializeEnum),
                "_updated", toIsoDateTime(customPage.getLastUpdated()));
    }

    public static class WsCustomPageData {

        private final String description;
        private final boolean isActive;

        public WsCustomPageData(
                @JsonProperty("description") String description,
                @JsonProperty("active") boolean active) {
            this.description = description;
            this.isActive = active;
        }

    }
}
