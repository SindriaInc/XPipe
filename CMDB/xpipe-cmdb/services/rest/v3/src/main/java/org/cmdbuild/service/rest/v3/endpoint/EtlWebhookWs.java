package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.etl.webhook.WebhookConfigImpl;
import org.cmdbuild.etl.webhook.WebhookConfigImpl.WebhookConfigImplBuilder;
import org.cmdbuild.etl.webhook.WebhookMethod;
import org.cmdbuild.etl.webhook.WebhookService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("etl/webhook/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EtlWebhookWs {

    private final WebhookService service;
    private final ClasseRepository repository;

    public EtlWebhookWs(WebhookService service, ClasseRepository repository) {
        this.service = checkNotNull(service);
        this.repository = checkNotNull(repository);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, WsQueryOptions wsQueryOptions) {
        return getMany(viewMode, service.getAll(), wsQueryOptions);
    }

    @GET
    @Path("{webhookId}/")
    public Object readOne(@PathParam("webhookId") String idOrCode) {
        WebhookConfig webhook = service.getByName(idOrCode);
        return response(serializeDetailedWebhook(webhook, repository.getClasse(webhook.getTarget())));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(WsEtlWebhookData data) {
        return response(serializeDetailedWebhook(service.create(data.toWebhookTrigger().build()), repository.getClasse(data.target)));
    }

    @PUT
    @Path("{webhookId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(@PathParam("webhookId") String webhookId, WsEtlWebhookData data) {
        return response(serializeDetailedWebhook(service.update(data.toWebhookTrigger().withCode(webhookId).build()), repository.getClasse(data.target)));
    }

    @DELETE
    @Path("{webhookId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("webhookId") String templateName) {
        service.delete(templateName);
        return success();
    }

    private Object getMany(String viewMode, List<WebhookConfig> webhooks, WsQueryOptions wsQueryOptions) {
        List<Map<String, Object>> list = list(webhooks)
                .withOnly(isAdminViewMode(viewMode) ? Predicates.alwaysTrue() : WebhookConfig::isActive)
                .map(wh -> {
                    return wsQueryOptions.isDetailed() ? serializeDetailedWebhook(wh, repository.getClasse(wh.getTarget())) : serializeBasicWebhook(wh);
                })
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter()));
        return response(paged(list, wsQueryOptions.getQuery()));
    }

    private static FluentMap serializeBasicWebhook(WebhookConfig webhook) {
        return map(
                "_id", webhook.getCode(),
                "code", webhook.getCode(),
                "description", webhook.getDescription(),
                "event", webhook.getEvents().stream().collect(joining(", ")),
                "target", webhook.getTarget(),
                "method", serializeEnum(webhook.getMethod()),
                "url", webhook.getUrl(),
                "active", webhook.isActive()
        );
    }

    private static FluentMap serializeDetailedWebhook(WebhookConfig webhook, Classe target) {
        return serializeBasicWebhook(webhook).with(map(
                "_target_type", getTargetType(target),
                "_target_description", target.getDescription(),
                "headers", webhook.getHeaders() == null ? null : unflattenMaps(fromJson(webhook.getHeaders(), MAP_OF_STRINGS)),
                "body", webhook.getBody() == null ? null : unflattenMaps(fromJson(webhook.getBody(), MAP_OF_STRINGS)),
                "language", webhook.getLanguage()
        ));
    }

    private static String getTargetType(Classe target) {
        return switch (target.getEtType()) {
            case ET_CLASS ->
                target.isProcess() ? "process" : "class";
            case ET_DOMAIN ->
                "domain";
            default ->
                throw new UnsupportedOperationException("target type not supported");
        };
    }

    private static class WsEtlWebhookData {

        private final String code, description, event, target, method, url, language;
        private final JsonNode headers, body;
        private final Boolean active;

        public WsEtlWebhookData(
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("event") String event,
                @JsonProperty("target") String target,
                @JsonProperty("method") String method,
                @JsonProperty("url") String url,
                @JsonProperty("headers") JsonNode headers,
                @JsonProperty("body") JsonNode body,
                @JsonProperty("language") String language,
                @JsonProperty("active") Boolean active) { // TODO handle filter?
            this.code = code;
            this.description = description;
            this.event = event;
            this.target = target;
            this.method = method;
            this.url = url;
            this.headers = headers;
            this.body = body;
            this.language = language;
            this.active = active;
        }

        public WebhookConfigImplBuilder toWebhookTrigger() {
            return WebhookConfigImpl.builder()
                    .withCode(code)
                    .withDescription(description)
                    .withEvents(event)
                    .withTarget(target)
                    .withMethod(parseEnum(method, WebhookMethod.class))
                    .withUrl(url)
                    .withHeaders(headers == null ? null : toJson(headers))
                    .withBody(body == null ? null : toJson(body))
                    .withLanguage(language)
                    .withActive(active);
        }
    }
}
