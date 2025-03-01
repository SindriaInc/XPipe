package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
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
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_GIS_MODIFY_AUTHORITY;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeConfigImpl;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisAttributeImpl.GisAttributeImplBuilder;
import org.cmdbuild.gis.GisAttributeType;
import org.cmdbuild.gis.GisService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTRIBUTE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtml;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/geoattributes/")
@Produces(APPLICATION_JSON)
public class GeoAttributeWs {

    private final static String TYPE_GEOMETRY = "geometry";

    private final GisService service;
    private final CoreConfiguration coreConfiguration;
    private final EasyuploadService easyuploadService;
    private final UserClassService userClassService;
    private final TranslationService translationService;

    public GeoAttributeWs(GisService service, CoreConfiguration coreConfiguration, EasyuploadService easyuploadService, UserClassService userClassService, TranslationService translationService) {
        this.service = checkNotNull(service);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.userClassService = checkNotNull(userClassService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readAllAttributes(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(START) Integer offset,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("visible") @DefaultValue(FALSE) Boolean visible) {
        List<GisAttribute> elements;
        if (equal(classId, "_ANY")) {
            elements = service.getGisAttributes();
        } else if (visible) {
            elements = service.getGisAttributesVisibleFromClass(classId);
        } else {
            elements = service.getGisAttributesByOwnerClassIncludeInherited(classId);
        }
        PagedElements<GisAttribute> paged = paged(elements, offset, limit);
        if (isAdminViewMode(viewMode)) {
            return response(paged.stream().map(this::serializeGisAttribute).collect(toList()), paged.totalSize());
        } else {
            List<Object> filteredAttributes = paged.stream()
                    .filter((a) -> userClassService.isActiveAndUserCanRead(a.getOwnerClassName()) && a.isActive() && userClassService.getUserClass(a.getOwnerClassName()).hasGisAttributeReadPermission(a.getLayerName()))
                    .map(this::serializeGisAttribute).collect(toList());
            return response(filteredAttributes, filteredAttributes.size());
        }
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object reorder(@PathParam(CLASS_ID) String classId, List<Long> attrOrder) {
        checkArgument(equal(classId, "_ANY"), "service available only for _ANY classes");
        List<GisAttribute> attrs = service.updateGisAttributesOrder(attrOrder);
        return response(attrs.stream().map(this::serializeGisAttribute));
    }

    @GET
    @Path("{" + ATTRIBUTE + "}/")
    public Object readAttribute(@PathParam(CLASS_ID) String classId, @PathParam(ATTRIBUTE) String attributeId) {
        GisAttribute layer = service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId);
        checkArgument(userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeReadPermission(layer.getLayerName()), format("User not allowed to access the specified GeoAttribute < %s >", layer.getLayerName()));
        return response(serializeGisAttribute(layer));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object create(@PathParam(CLASS_ID) String classId, WsGeoAttribute attributeData) {
        GisAttribute layer = toGisAttribute(attributeData, classId).build();
        layer = service.createGisAttribute(layer);
        return response(serializeGisAttribute(layer));
    }

    @POST
    @Path("visibility")
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object updateVisibility(@PathParam(CLASS_ID) String classId, Map<Long, Boolean> geoAttributes) {
        service.updateGeoAttributesVisibilityForClass(classId, geoAttributes);
        return response(list(service.getGisAttributesVisibleFromClass(classId)).map(this::serializeGisAttribute));
    }

    @PUT
    @Path("{" + ATTRIBUTE + "}/")
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(ATTRIBUTE) String attributeId, WsGeoAttribute attributeData) {
        GisAttribute layer = toGisAttribute(attributeData, classId).withId(service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId).getId()).build();
        layer = service.updateGisAttribute(layer);
        return response(serializeGisAttribute(layer));
    }

    @DELETE
    @Path("{" + ATTRIBUTE + "}/")
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(ATTRIBUTE) String attributeId) {
        service.deleteGisAttribute(classId, service.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId).getLayerName());
        return success();
    }

    private Object serializeGisAttribute(GisAttribute layer) {
        return map(
                "_id", layer.getId(),
                "name", layer.getLayerName(),
                "owner_type", layer.getOwnerClassName(),
                "active", layer.isActive(),
                "type", layer.isPostgis() ? TYPE_GEOMETRY : serializeEnum(layer.getType()),
                "subtype", serializeEnum(layer.getType()),
                "description", layer.getDescription(),
                "_description_translation", translationService.translateGisAttributeDescription(layer),
                "index", layer.getIndex(),
                "visibility", layer.getVisibilityMap(),
                "zoomMin", layer.getMinimumZoom(),
                "zoomMax", layer.getMaximumZoom(),
                "zoomDef", layer.getDefaultZoom(),
                "_beginDate", CmDateUtils.toIsoDateTime(layer.getBeginDate()),
                "_is_geometry", layer.isPostgis(),
                "_is_coverage", layer.isGeoserver(),
                "style", map(layer.getMapStyleMap()).withoutKeys("externalGraphic")).accept((m) -> {
            String icon = toStringOrNull(layer.getMapStyleMap().get("externalGraphic"));
            if (isNotBlank(icon)) {
                m.put("_icon", Optional.ofNullable(easyuploadService.getByPathOrNull(icon)).map(EasyuploadItem::getId).orElse(null));
            }
            if (layer.isPostgis()) {
                m.put("writable", userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeWritePermission(layer.getLayerName()));
            } else {
                m.put("writable", service.isGeoserverEnabled() ? userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeWritePermission(layer.getLayerName()) : false);
            }
        }).with("infoWindowEnabled", layer.getConfig().getInfoWindowEnabled(),
                "infoWindowContent", layer.getConfig().getInfoWindowContent(),
                "infoWindowImage", layer.getConfig().getInfoWindowImage());
    }

    private GisAttributeImplBuilder toGisAttribute(WsGeoAttribute data, String classId) {
        return data.toGisAttribute(coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE)).withOwnerClassName(classId).accept((b) -> {
            Map<String, Object> styleMap = map(data.style);
            if (isNotNullAndGtZero(data.icon)) {
                styleMap.put("externalGraphic", easyuploadService.getById(data.icon).getPath());
            }
            b.withMapStyle(styleMap);
        });
    }

    public static class WsGeoAttribute {

        private final String name;
        private final Long icon;
        private final String description;
        private final GisAttributeType type;
        private final boolean active;
        private final Integer index, zoomMin, zoomDef, zoomMax;
        private final Map<String, Boolean> visibility;
        private final Map<String, Object> style;
        private final boolean infoWindowEnabled;
        private final String infoWindowContent;
        private final String infoWindowImage;

        public WsGeoAttribute(
                @JsonProperty("_icon") Long icon,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("active") boolean active,
                @JsonProperty("type") String type,
                @JsonProperty("subtype") String subtype,
                @JsonProperty("index") Integer index,
                @JsonProperty("zoomMin") Integer zoomMin,
                @JsonProperty("zoomDef") Integer zoomDef,
                @JsonProperty("zoomMax") Integer zoomMax,
                @JsonProperty("visibility") Map<String, Boolean> visibility,
                @JsonProperty("style") Map<String, Object> style,
                @JsonProperty("infoWindowEnabled") boolean infoWindowEnabled,
                @JsonProperty("infoWindowContent") String infoWindowContent,
                @JsonProperty("infoWindowImage") String infoWindowImage) {
            this.icon = icon;
            this.name = name;
            this.description = description;
            this.active = active;
            this.type = parseEnum(equal(type, TYPE_GEOMETRY) ? subtype : type, GisAttributeType.class);
            this.index = index;
            this.zoomMin = zoomMin;
            this.zoomDef = zoomDef;
            this.zoomMax = zoomMax;
            this.visibility = map(visibility).immutable();
            this.style = map(style).immutable();
            this.infoWindowEnabled = infoWindowEnabled;
            this.infoWindowContent = infoWindowContent;
            this.infoWindowImage = infoWindowImage;
        }

        public GisAttributeImplBuilder toGisAttribute(boolean sanitizeHtml) {
            return GisAttributeImpl.builder()
                    .withLayerName(name)
                    .withDescription(description)
                    .withActive(active)
                    .withType(type)
                    .withIndex(index)
                    .withMinimumZoom(zoomMin)
                    .withDefaultZoom(zoomDef)
                    .withMaximumZoom(zoomMax)
                    .withVisibility(visibility)
                    .withConfig(GisAttributeConfigImpl.builder()
                            .withInfoWindowEnabled(infoWindowEnabled)
                            .withInfoWindowContent(sanitizeHtml ? sanitizeHtml(infoWindowContent) : infoWindowContent)
                            .withInfoWindowImage(infoWindowImage)
                            .build());
        }

    }
}
