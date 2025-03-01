package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.FilterType;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.HeaderParam;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import org.cmdbuild.config.EmailConfiguration;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.beans.EmailSignatureImpl;
import org.cmdbuild.email.beans.EmailSignatureImpl.EmailSignatureImplBuilder;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("email/signatures/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmailSignatureWs {

    private final EmailConfiguration config;
    private final EmailSignatureService service;
    private final ObjectTranslationService translationService;

    public EmailSignatureWs(EmailConfiguration config, EmailSignatureService service, ObjectTranslationService translationService) {
        this.config = checkNotNull(config);
        this.service = checkNotNull(service);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed) {
        List<EmailSignature> list = service.getAll();
        if (!isAdminViewMode(viewMode)) {
            list = list(list).withOnly(EmailSignature::isActive);
        }
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        if (!sorter.isNoop()) {
            list = sorted(list, sorter, (key, template) -> {
                switch (key) {
                    case "code":
                        return template.getCode();
                    case "description":
                        return template.getDescription();
                    default:
                        throw new IllegalArgumentException("unsupported filter key = " + key);
                }
            });
        }
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<EmailSignature>builder()
                    .withKeyToValueFunction((key, template) -> {
                        switch (checkNotBlank(key)) {
                            case "code":
                                return template.getCode();
                            case "description":
                                return template.getDescription();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        return response(paged(list.stream().map(c -> serializeSignature(c, detailed)).collect(toList()), offset, limit));
    }

    @GET
    @Path("{signatureId}/")
    public Object read(@PathParam("signatureId") String id) {
        return response(serializeSignature(service.getOne(id), true));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object create(WsEmailSignatureData data) {
        return response(serializeSignature(service.create(data.toEmailSignature().build()), true));
    }

    @PUT
    @Path("{signatureId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(@PathParam("signatureId") Long signatureId, WsEmailSignatureData data) {
        return response(serializeSignature(service.update(data.toEmailSignature().withId(signatureId).build()), true));
    }

    @DELETE
    @Path("{signatureId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("signatureId") Long signatureId) {
        service.delete(signatureId);
        return success();
    }

    private Object serializeSignature(EmailSignature s, boolean detailed) {
        return map(
                "_id", s.getId(),
                "code", s.getCode(),
                "description", s.getDescription(),
                "_description_translation", translationService.translateEmailSignatureDescription(s.getCode(), s.getDescription()),
                "active", s.isActive(),
                "_default", equal(s.getCode(), config.getDefaultEmailSignature())).accept(m -> {
            if (detailed) {
                m.put(
                        "content_html", s.getContentHtml(),
                        "_content_html_translation", translationService.translateEmailSignatureContenthtml(s.getCode(), s.getContentHtml()));
            }
        });
    }

    public static class WsEmailSignatureData {

        private final String code, description, contentHtml;
        private final Boolean active;

        public WsEmailSignatureData(
                @JsonProperty("active") Boolean active,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("content_html") String contentHtml) {
            this.code = code;
            this.active = active;
            this.description = description;
            this.contentHtml = contentHtml;
        }

        public EmailSignatureImplBuilder toEmailSignature() {
            return EmailSignatureImpl.builder()
                    .withDescription(description)
                    .withCode(code)
                    .withContentHtml(contentHtml)
                    .withActive(active);
        }
    }
}
