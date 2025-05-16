package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import static com.google.common.collect.Streams.stream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.dashboard.inner.DashboardDataImpl;
import org.cmdbuild.dashboard.inner.DashboardDataImpl.DashboardDataImplBuilder;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("dashboards/")
@Produces(APPLICATION_JSON)
public class DashboardWs {

    private final DashboardService dashboardService;
    private final ObjectTranslationService translationService;

    public DashboardWs(ObjectTranslationService translationService, DashboardService dashboardService) {
        this.translationService = checkNotNull(translationService);
        this.dashboardService = checkNotNull(dashboardService);
    }

    @GET
    @Path(EMPTY)
    public Object getAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<DashboardData> list = isAdminViewMode(viewMode) ? dashboardService.getAll() : dashboardService.getActiveForCurrentUser();
        return response(paged(list, offset, limit).map(detailed ? this::serializeDetailedDashboard : this::serializeBasicDashboard));
    }

    @GET
    @Path("{id}/")
    public Object readOne(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("id") String idOrCode) {
        return response(serializeDetailedDashboard(isAdminViewMode(viewMode) ? dashboardService.getByIdOrCode(idOrCode) : dashboardService.getForUserByIdOrCode(idOrCode)));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(WsDashboardData data) {
        DashboardData dashboard = data.toDashboard().build();
        dashboard = dashboardService.create(dashboard);
        return response(serializeDetailedDashboard(dashboard));
    }

    @PUT
    @Path("{id}/")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(@PathParam("id") Long id, WsDashboardData data) {
        DashboardData dashboard = data.toDashboard().withId(id).build();
        dashboard = dashboardService.update(dashboard);
        return response(serializeDetailedDashboard(dashboard));
    }

    @DELETE
    @Path("{id}/")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        dashboardService.delete(id);
        return success();
    }

    private FluentMap<String, Object> serializeBasicDashboard(DashboardData dashboard) {
        return map(
                "_id", dashboard.getId(),
                "name", dashboard.getCode(),
                "description", dashboard.getDescription(),
                "_description_translation", translationService.translateDashboardDescription(dashboard.getCode(), dashboard.getDescription()),
                "active", dashboard.isActive()
        );
    }

    private FluentMap<String, Object> serializeDetailedDashboard(DashboardData dashboard) {
        return new DashboardSerializationHelper(dashboard).serializeDetailedDashboard();
    }

    private class DashboardSerializationHelper {

        private final DashboardData dashboard;

        private int cqlIndex = 0;

        public DashboardSerializationHelper(DashboardData dashboard) {
            this.dashboard = checkNotNull(dashboard);
        }

        public FluentMap<String, Object> serializeDetailedDashboard() {
            return serializeBasicDashboard(dashboard).with(
                    "charts", serializeCharts(),
                    "layout", fromJson(dashboard.getConfig(), Map.class).get("layout")//TODO improve this
            );
        }

        private JsonNode serializeCharts() { //note: this MUST match the order in DashboardUtils.getEcqlExprsInOrder for ecql filter processing !!
            JsonNode charts = fromJson(dashboard.getConfig(), JsonNode.class).get("charts");
            stream(charts.elements()).map(ObjectNode.class::cast).forEach(chart -> {
                if (chart.hasNonNull("description")) {
                    chart.put("_description_translation",
                            translationService.translateDashboardChartDescription(dashboard.getCode(), chart.get("_id").asText(), chart.get("description").asText()));
                }
                if (chart.hasNonNull("valueAxisLabel")) {
                    chart.put("_valueAxisLabel_translation",
                            translationService.translateDashboardChartValueAxisLabel(dashboard.getCode(), chart.get("_id").asText(), chart.get("valueAxisLabel").asText()));
                }
                if (chart.hasNonNull("categoryAxisLabel")) {
                    chart.put("_categoryAxisLabel_translation",
                            translationService.translateDashboardChartCategoryAxisLabel(dashboard.getCode(), chart.get("_id").asText(), chart.get("categoryAxisLabel").asText()));
                }
                if (chart.hasNonNull("labelField")) {
                    chart.put("_labelField_translation",
                            translationService.translateDashboardChartLabelField(dashboard.getCode(), chart.get("_id").asText(), chart.get("labelField").asText()));
                }
                if (chart.hasNonNull("dataSourceFilter") && !chart.get("dataSourceFilter").asText().isBlank()) {
                    ((ObjectNode) chart).set("ecqlDataSourceFilter", new ObjectMapper().valueToTree(createEcqlFilterData(chart.get("dataSourceFilter").asText())));
                }
                if (chart.hasNonNull("dataSourceParameters")) {
                    ArrayNode dataSourceParameters = (ArrayNode) chart.get("dataSourceParameters");
                    int i = 0;
                    for (JsonNode dataSourceParameter : dataSourceParameters) {
                        String name = Optional.ofNullable(dataSourceParameter.get("name")).map(JsonNode::textValue).orElse(null);
                        if (isNotBlank(name)) {
                            String description = Optional.ofNullable(dataSourceParameter.get("description")).map(JsonNode::textValue).map(Strings::emptyToNull).orElse(name);
                            ((ObjectNode) dataSourceParameter).put("_description_translation",
                                    translationService.translateDashboardChartDataSourceParameter(dashboard.getCode(), chart.get("_id").asText(), Integer.toString(i++), description));
                        }
                        if (dataSourceParameter.hasNonNull("filter")) {
                            if (dataSourceParameter.get("filter").hasNonNull("expression")) {
                                if (!dataSourceParameter.get("filter").get("expression").asText().isBlank()) {
                                    ObjectMapper mapper = new ObjectMapper();
                                    ((ObjectNode) dataSourceParameter).set("ecqlFilter", mapper.valueToTree(createEcqlFilterData(dataSourceParameter.get("filter").get("expression").asText())));
                                }
                            }
                        }
                    }
                }
            });
            return charts;
        }

        private Map<String, Object> createEcqlFilterData(String cqlFilter) {
            EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(new EcqlExpressionImpl(cqlFilter));
            String ecqlId = EcqlUtils.buildDashboardEcqlId(dashboard.getCode(), cqlIndex++);
            Map<String, Object> ecqlData = map("id", ecqlId, "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings()));
            return ecqlData;
        }
    }

    public static class WsDashboardData {

        private final String name, description;
        private final Boolean active;
        private final Object config;

        public WsDashboardData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("active") Boolean active,
                @JsonProperty("charts") Object charts,
                @JsonProperty("layout") Object layout) {
            this.name = name;
            this.description = description;
            this.active = active;
            this.config = map("charts", charts, "layout", layout);//TODO improve this
        }

        public DashboardDataImplBuilder toDashboard() {
            return DashboardDataImpl.builder()
                    .withCode(name)
                    .withDescription(description)
                    .withActive(active)
                    .withConfig(toJson(config));
        }

    }
}
