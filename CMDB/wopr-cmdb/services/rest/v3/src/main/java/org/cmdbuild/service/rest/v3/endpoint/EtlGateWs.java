package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import org.cmdbuild.etl.gate.inner.EtlGateImpl.EtlGateImplBuilder;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("etl/gates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EtlGateWs {

    private final EtlGateService gateService;
    private final EtlTemplateService templateService;

    public EtlGateWs(EtlGateService gateService, EtlTemplateService templateService) {
        this.gateService = checkNotNull(gateService);
        this.templateService = checkNotNull(templateService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(WsQueryOptions wsQueryOptions, @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates) {
        return response(paged(list(gateService.getAllForCurrentUser()).map(e -> serializeGate(e, wsQueryOptions.isDetailed(), includeEtlTemplates)).withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("by-class/{classId}")
    public Object readAllForClass(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, WsQueryOptions wsQueryOptions, @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates) {
        List<Map<String, Object>> gates = list(gateService.getAllForCurrentUser()).withOnly(e -> e.getShowOnClasses().contains(checkNotBlank(classId))).map(e -> serializeGate(e, wsQueryOptions.isDetailed(), includeEtlTemplates));
        wsQueryOptions.getQuery().getFilter().checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (wsQueryOptions.getQuery().getFilter().hasAttributeFilter()) {
            gates = AttributeFilterProcessor.builder().withFilter(wsQueryOptions.getQuery().getFilter().getAttributeFilter()).withKeyToValueFunction((k, m) -> ((Map) m).get(k)).filter(gates);
        }
        return response(paged(gates, wsQueryOptions.getQuery().getOffset(), wsQueryOptions.getQuery().getLimit()));
    }

    @GET
    @Path("{code}/")
    public Object read(@PathParam("code") String code, @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates) {
        EtlGate gate = gateService.getByCodeForCurrentUser(code);
        return response(serializeGate(gate, true, includeEtlTemplates));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(WsImportExportGateData data) {
        return response(serializeDetailedGate(gateService.create(data.toEtlGate().build())));
    }

    @PUT
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(@PathParam("code") String code, WsImportExportGateData data) {
        return response(serializeDetailedGate(gateService.update(data.toEtlGate().withCode(code).build())));
    }

    @DELETE
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("code") String code) {
        gateService.delete(code);
        return success();
    }

    private Map<String, Object> serializeGate(EtlGate gate, boolean detailed, boolean includeEtlTemplates) {
        return (detailed ? serializeDetailedGate(gate) : serializeBasicGate(gate)).accept(m -> {
            if (includeEtlTemplates) {
                m.put("_templates", gate.getAllTemplates().stream().map(templateService::getTemplateByName).filter(t -> t.isActive()).map(EtlTemplateWs::serializeDetailedTemplate).collect(toImmutableList()));
            }
        });
    }

    private FluentMap<String, Object> serializeBasicGate(EtlGate gate) {
        return (FluentMap) map(
                "_id", gate.getCode(),
                "code", gate.getCode(),
                "description", gate.getDescription(),
                "allowPublicAccess", gate.getAllowPublicAccess(),
                "processingMode", serializeEnum(gate.getProcessingMode()),
                "enabled", gate.isEnabled(),
                "_has_single_handler", gate.hasSingleHandler()
        ).accept(m -> {
            if (gate.hasSingleHandler()) {
                m.put("_handler_type", gate.getSingleHandlerType());
            }
        });
    }

    private FluentMap<String, Object> serializeDetailedGate(EtlGate gate) {
        return serializeBasicGate(gate).with(
                "config", map(gate.getConfig()),
                "handlers", list(gate.getHandlers()).map(h -> map(h.getConfig())
                .withoutKeys(k -> gate.getConfig().containsKey(k) && equal(gate.getConfig(k), h.getConfig(k))).with("type", h.getType()))
        ).accept(m -> {
            if (gate.hasSingleHandler()) {
                m.put("_handler_config", gate.getSingleHandler().getConfig());
            }
        });
    }

    public static class WsImportExportGateData {

        private final String code, description;
        private final Boolean enabled, allowPublicAccess;
        private final Map<String, String> config;
        private final EtlProcessingMode processingMode;
        private final List<Map<String, String>> handlers;

        public WsImportExportGateData(
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("processingMode") String processingMode,
                @JsonProperty("allowPublicAccess") Boolean allowPublicAccess,
                @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("config") Map<String, String> config,
                @JsonProperty("handlers") List<Map<String, String>> handlers) {
            this.code = code;
            this.description = description;
            this.processingMode = parseEnumOrNull(processingMode, EtlProcessingMode.class);
            this.allowPublicAccess = allowPublicAccess;
            this.enabled = enabled;
            this.config = config;
            this.handlers = handlers;
        }

        public EtlGateImplBuilder toEtlGate() {
            return EtlGateImpl.builder()
                    .withCode(code)
                    .withDescription(description)
                    .withAllowPublicAccess(allowPublicAccess)
                    .withConfig(config)
                    .withEnabled(enabled)
                    .withProcessingMode(processingMode)
                    .withHandlersConfig(handlers);
        }
    }

}
