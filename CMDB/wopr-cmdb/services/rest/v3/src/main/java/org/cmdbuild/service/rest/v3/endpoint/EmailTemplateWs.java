package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
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
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl.EmailTemplateImplBuilder;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.service.rest.common.serializationhelpers.EmailTemplateSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.model.WsReportConfigData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("email/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmailTemplateWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailTemplateService service;
    private final EmailTemplateSerializationHelper templateHelper;
    private final ReportService reportService;

    public EmailTemplateWs(EmailTemplateService service, EmailTemplateSerializationHelper templateHelper, org.cmdbuild.report.ReportService reportService) {
        this.service = checkNotNull(service);
        this.templateHelper = checkNotNull(templateHelper);
        this.reportService = reportService;
    }

    @GET
    @Path("by-class/{classId}")
    public Object readAllForClass(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings) {
        return doReadAll(viewMode, filterStr, sort, limit, offset, detailed, includeBindings, checkNotBlank(classId));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings) {
        return doReadAll(viewMode, filterStr, sort, limit, offset, detailed, includeBindings, null);
    }

    @GET
    @Path("{templateId}/")
    public Object read(@PathParam("templateId") String id, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings) {
        return response(serializeTemplate(service.getByNameOrId(id), true, includeBindings));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object create(WsEmailTemplateData data) {
        return response(serializeDetailedTemplate(service.createEmailTemplate(skipBatchReports(data.toEmailTemplate().build()))));
    }

    @PUT
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(@PathParam("templateId") Long templateId, WsEmailTemplateData data) {
        return response(serializeDetailedTemplate(service.updateEmailTemplate(data.toEmailTemplate().withId(templateId).build())));
    }

    @DELETE
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("templateId") Long templateId) {
        service.deleteEmailTemplate(templateId);
        return success();
    }

    private Object doReadAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed, @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings, @Nullable String classId) {
        List<EmailTemplate> list = isAdminViewMode(viewMode) ? service.getAll() : service.getAllActive();
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        if (!sorter.isNoop()) {
            list = sorted(list, sorter, (key, template) -> toStringOrNull(serializeBasicTemplate(template).get(key)));//TODO improve this
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<EmailTemplate>builder()
                    .withKeyToValueFunction((key, template) -> toStringOrNull(serializeBasicTemplate(template).get(key)))//TODO improve this
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        if (isNotBlank(classId)) {
            list.removeIf(t -> !t.getShowOnClasses().isEmpty() && !t.getShowOnClasses().contains(classId));
        }
        return response(paged(list.stream().map(c -> serializeTemplate(c, detailed, includeBindings)).collect(toList()), offset, limit));
    }

    private FluentMap<String, Object> serializeTemplate(EmailTemplate t, boolean detailed, boolean includeBindings) {
        return (detailed ? serializeDetailedTemplate(t) : serializeBasicTemplate(t)).accept(m -> {
            templateHelper.serializeEmailTemplateBindings(t, includeBindings, m::put);
            templateHelper.serializeEmailTemplateTranslation(t, m::put);
        });
    }

    private FluentMap<String, Object> serializeBasicTemplate(EmailTemplate t) {
        return map(
                "_id", firstNotBlank(t.getId(), t.getCode()),
                "name", t.getCode(),
                "description", t.getDescription(),
                "provider", t.getNotificationProvider(),
                "_can_write", isNotNullAndGtZero(t.getId())//TODO improve this, templates from file
        );
    }

    private FluentMap<String, Object> serializeDetailedTemplate(EmailTemplate t) {
        return serializeBasicTemplate(t).with(
                "from", t.getFrom(),
                "to", t.getTo(),
                "cc", t.getCc(),
                "bcc", t.getBcc(),
                "subject", t.getSubject(),
                "body", t.getContent(),
                "contentType", t.getContentType(),
                "account", t.getAccount(),
                "signature", t.getSignature(),
                "keepSynchronization", t.getKeepSynchronization(),
                "promptSynchronization", t.getPromptSynchronization(),
                "delay", t.getDelay(),
                "data", t.getMeta(),
                "active", t.isActive(),
                "showOnClasses", Joiner.on(",").join(t.getShowOnClasses()),
                "reports", list(t.getReports()).map(ReportConfigImpl::toConfig)
        );
    }

    private EmailTemplate skipBatchReports(EmailTemplate emailTemplate) {
        List<ReportConfig> realTimeReportCodes = list(emailTemplate.getReports()).without(r -> {
            boolean isBatch = reportService.getByCode(r.getCode()).isBatchReport();
            if (isBatch) {
                logger.warn("unable to use batch report =< {} > for email template =< {} >", r.getCode(), emailTemplate.getCode());
            }
            return isBatch;
        });
        return EmailTemplateImpl.copyOf(emailTemplate).withReports(realTimeReportCodes).build();
    }

    public static class WsEmailTemplateData {

        private final Long delay, account, signature;
        private final String from, to, cc, bcc, subject, body, name, description, contentType, provider;
        private final Boolean keepSynchronization, promptSynchronization, active;
        private final Map<String, String> data;
        private final List<WsReportConfigData> reports;

        public WsEmailTemplateData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("delay") Long delay,
                @JsonProperty("from") String from,
                @JsonProperty("to") String to,
                @JsonProperty("cc") String cc,
                @JsonProperty("bcc") String bcc,
                @JsonProperty("subject") String subject,
                @JsonProperty("contentType") String contentType,
                @JsonProperty("body") String body,
                @JsonProperty("account") Long account,
                @JsonProperty("signature") Long signature,
                @JsonProperty("keepSynchronization") Boolean keepSynchronization,
                @JsonProperty("promptSynchronization") Boolean promptSynchronization,
                @JsonProperty("provider") String provider,
                @JsonProperty("active") Boolean active,
                @JsonProperty("showOnClasses") String showOnClasses,
                @JsonProperty("data") Map<String, String> data,
                @JsonProperty("reports") List<WsReportConfigData> reports) {
            this.delay = delay;
            this.from = from;
            this.to = to;
            this.cc = cc;
            this.bcc = bcc;
            this.subject = subject;
            this.body = body;
            this.account = account;
            this.signature = signature;
            this.name = name;
            this.contentType = contentType;
            this.description = description;
            this.keepSynchronization = keepSynchronization;
            this.promptSynchronization = promptSynchronization;
            this.provider = provider;
            this.active = active;
            this.data = map(nullToEmpty(data)).with("showOnClasses", showOnClasses);//TODO change this to checknotnull
            this.reports = ImmutableList.copyOf(CmNullableUtils.firstNotNull(reports, Collections.emptyList()));
        }

        public EmailTemplateImplBuilder toEmailTemplate() {
            return EmailTemplateImpl.builder()
                    .withDelay(delay)
                    .withAccount(account)
                    .withBcc(bcc)
                    .withCc(cc)
                    .withContent(body)
                    .withDelay(delay)
                    .withFrom(from)
                    .withKeepSynchronization(keepSynchronization)
                    .withPromptSynchronization(promptSynchronization)
                    .withSubject(subject)
                    .withTo(to)
                    .withDescription(description)
                    .withCode(name)
                    .withContentType(contentType)
                    .withMeta(data)
                    .withSignature(signature)
                    .withNotificationProvider(provider)
                    .withActive(active)
                    .withReports(reports.stream().map((r) -> r.buildReportConfig().build()).collect(Collectors.toList()));
        }
    } // end WsEmailTemplateData class
}
