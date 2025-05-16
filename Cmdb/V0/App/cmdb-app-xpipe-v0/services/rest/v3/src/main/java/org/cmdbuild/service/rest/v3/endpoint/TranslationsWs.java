package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.filterKeys;
import com.google.common.collect.Ordering;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
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
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.translation.dao.Translation;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.preload.PreloadService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import org.cmdbuild.translation.ExportRecord;
import org.cmdbuild.translation.TranslationExportHelper;
import static org.cmdbuild.translation.TranslationSection.TS_ALL;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;

@Path("translations/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TranslationsWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TranslationService translationService;
    private final PreloadService preloadService;

    public TranslationsWs(TranslationService translationService, PreloadService preloadService) {
        this.translationService = checkNotNull(translationService);
        this.preloadService = checkNotNull(preloadService);
    }

    @GET
    @Path("")
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAll(@Nullable @QueryParam(LIMIT) Integer limit, @Nullable @QueryParam(START) Integer offset, @Nullable @QueryParam(FILTER) String filter) {
        PagedElements<Translation> translations = translationService.getTranslations(filter, offset, limit);
        return response(translations.map((t) -> map("code", t.getCode(), "lang", t.getLang(), "value", t.getValue())));
    }

    @GET
    @Path("loadTranslations")
    public Object loadAllTranslationsForLanguages(@QueryParam("lang") String languages) {
        translationService.loadTranslationsForLanguages(Splitter.on(";").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)));
        return response(success());
    }

    @GET
    @Path("by-code")
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAllAggregateByCode(
            @Nullable @QueryParam(LIMIT) Integer limit,
            @Nullable @QueryParam(START) Integer offset,
            @Nullable @QueryParam(FILTER) String filter,
            @Nullable @QueryParam("lang") String languages,
            @QueryParam("includeRecordsWithoutTranslation") @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation,
            @Nullable @QueryParam("section") String section) {
        TranslationExportHelper helper = translationService.exportHelper().withSection(parseEnumOrDefault(section, TS_ALL))
                .withLanguages(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)))
                .withEmptyRecordsForAllObjects(includeRecordsWithoutTranslation);
        Stream<ExportRecord> records = helper.exportRecords().stream();
        if (isNotBlank(filter)) {
            records = records.filter(r -> r.getCode().toLowerCase().contains(filter.toLowerCase().trim()));
        }
        return response(paged(records.sorted(Ordering.natural().onResultOf(ExportRecord::getCode)).collect(toList()), offset, limit).map((t) -> map(
                "code", t.getCode(),
                "default", t.getDefault(),
                "values", map(t.getTranslationsByLanguage()).withoutValues(Strings::isNullOrEmpty))));
    }

    @GET
    @Path("{code}/")
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getTranslationForKeyAndLang(@PathParam("code") String code, @Nullable @QueryParam("lang") String lang) {
        if (isNotBlank(lang)) {
            String value = translationService.getTranslationValueForCodeAndLang(code, lang);
            return response(map("code", code, "lang", lang, "value", value));
        } else {
            return serializeResponse(code, translationService.getTranslationValueMapByLangForCode(code));
        }
    }

    @PUT
    @Path("{code}/")
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object setTranslation(@PathParam("code") String code, Map<String, String> data) {
        filterKeys(data, not(equalTo("_id"))).forEach((k, v) -> {
            if (isNotBlank(v)) {
                translationService.setTranslation(code, k, v);
            } else {
                translationService.deleteTranslationIfExists(code, k);
            }
        });
        return serializeResponse(code, translationService.getTranslationValueMapByLangForCode(code));
    }

    @DELETE
    @Path("{code}/")
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object deleteTranslation(@PathParam("code") String code, @Nullable @QueryParam("lang") String lang) {
        if (isBlank(lang)) {
            translationService.deleteTranslations(code);
        } else {
            translationService.deleteTranslationIfExists(code, lang);
        }
        return success();
    }

    @GET
    @Path("export")
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public DataHandler export(
            @QueryParam("lang") String languages,
            @QueryParam("format") String format,
            @QueryParam(FILTER) String filter,
            @QueryParam("separator") String separator,
            @QueryParam("section") String section,
            @QueryParam("includeRecordsWithoutTranslation") @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation
    ) {
        checkArgument(isBlank(format) || equal(format.toLowerCase(), "csv"), "invalid format = %s", format);
        //TODO filter
        return translationService.exportHelper()
                .withLanguages(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(languages)))
                .withSeparator(separator)
                .withSection(parseEnumOrDefault(section, TS_ALL))
                .withEmptyRecordsForAllObjects(includeRecordsWithoutTranslation)
                .withIncludeRecordsWithoutDefault(false)
                .export();
    }

    @POST
    @Path("import")
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object importTranslations(@QueryParam("separator") String separator, @Multipart(FILE) DataHandler dataHandler) {
        translationService.importHelper().withSeparator(separator).importTranslations(dataHandler);
        logger.info("Running system preload");
        preloadService.runPreload();
        return success();
    }

    private Object serializeResponse(String code, Map<String, String> map) {
        return response(map("_id", code).with(map));
    }

}
