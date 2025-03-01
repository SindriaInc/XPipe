package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
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
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateImpl.EtlTemplateImplBuilder;
import org.cmdbuild.etl.loader.EtlTemplateColumnMode;
import org.cmdbuild.etl.loader.EtlFileFormat;
import org.cmdbuild.etl.loader.EtlMergeMode;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import org.cmdbuild.etl.loader.EtlTemplateType;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import jakarta.annotation.security.RolesAllowed;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_NO_MERGE;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_ID;
import org.cmdbuild.etl.loader.EtlTemplateConfig.EnableCreate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import static org.cmdbuild.etl.loader.EtlTemplateType.ETT_IMPORT_EXPORT;
import static org.cmdbuild.etl.utils.EtlResultUtils.etlProcessingResultToJsonObject;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.temp.TempInfoSource.TS_SECURE;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.etl.loader.EtlTemplateInlineProcessorService;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_PROCESS;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_VIEW;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

@Path("etl/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EtlTemplateWs {

    private final EtlTemplateService service;
    private final EtlTemplateInlineProcessorService inlineService;
    private final TempService temp;
    private final CardWsSerializationHelperv3 helper;

    public EtlTemplateWs(EtlTemplateService service, EtlTemplateInlineProcessorService inlineService, TempService temp, CardWsSerializationHelperv3 helper) {
        this.service = checkNotNull(service);
        this.inlineService = checkNotNull(inlineService);
        this.temp = checkNotNull(temp);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, WsQueryOptions wsQueryOptions) {
        return getMany(viewMode, service.getAllForUser(), wsQueryOptions);
    }

    @GET
    @Path("by-class/{classId}")
    public Object readAllForClass(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, WsQueryOptions wsQueryOptions, @QueryParam("include_related_domains") @DefaultValue(FALSE) boolean includeRelatedDomains) {
        return getMany(viewMode, includeRelatedDomains ? service.getForUserForTargetClassAndRelatedDomains(classId) : service.getForUserForTarget(ET_CLASS, classId), wsQueryOptions);
    }

    @GET
    @Path("by-process/{classId}")
    public Object readAllForProcess(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("classId") String classId, WsQueryOptions wsQueryOptions, @QueryParam("include_related_domains") @DefaultValue(FALSE) boolean includeRelatedDomains) {
        return getMany(viewMode, includeRelatedDomains ? service.getForUserForTargetClassAndRelatedDomains(classId) : service.getForUserForTarget(ET_PROCESS, classId), wsQueryOptions);
    }

    @GET
    @Path("by-view/{viewId}")
    public Object readAllForView(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("viewId") String viewId, WsQueryOptions wsQueryOptions) {
        return getMany(viewMode, service.getForUserForTarget(ET_VIEW, viewId), wsQueryOptions);
    }

    @GET
    @Path("{templateId}/")
    public Object readOne(@PathParam("templateId") String idOrCode) {
        EtlTemplate template = service.getForUserByCode(idOrCode);
        return response(serializeDetailedTemplate(template));
    }

    @GET
    @Path("{templateId}/export|{templateId}/export/{fileName}")
    public DataHandler executeExportTemplate(@PathParam("templateId") String idOrCode, @QueryParam(FILTER) String filterStr) {
        return new DataHandler(service.exportForUserDataWithTemplateAndFilter(idOrCode, filterStr));
    }

    @POST
    @Path("{templateId}/import")
    @Consumes(MULTIPART_FORM_DATA)
    public Object executeImportTemplate(@PathParam("templateId") String idOrCode, @Multipart(value = FILE, required = true) DataHandler dataHandler, @QueryParam("detailed_report") @DefaultValue(FALSE) Boolean detailedReport) {
        EtlTemplate template = service.getForUserByCode(idOrCode);
        EtlProcessingResult result = service.importForUserDataWithTemplate(toDataSource(dataHandler), template);
        DataSource report = service.buildImportResultReport(result, template);
        return response(etlProcessingResultToJsonObject(result, detailedReport).with(
                "report", map(
                        "contentType", report.getContentType(),
                        "filename", report.getName(),
                        "content", temp.putTempData(report, TS_SECURE)
                )));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(WsEtlTemplateData data) {
        return response(serializeDetailedTemplate(service.create(data.toImportExportTemplate().build())));
    }

    @PUT
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(@PathParam("templateId") String templateId, WsEtlTemplateData data) {
        return response(serializeDetailedTemplate(service.update(data.toImportExportTemplate().withCode(templateId).build())));
    }

    @DELETE
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("templateId") String templateName) {
        service.delete(templateName);
        return success();
    }

    @POST
    @Path("inline/export|inline/export/{fileName}")
    public DataHandler executeInlineExportTemplate(@Multipart(value = "data", required = true) String data, @Multipart(value = "config", required = true) WsEtlTemplateData config) {
        return toDataHandler(inlineService.exportDataInline(fromJson(data, LIST_OF_MAP_OF_OBJECTS), config.toInlineModel(), config.toInlineTemplate()));
    }

    @POST
    @Path("inline/import")
    @Consumes(MULTIPART_FORM_DATA)
    public Object executeInlineImportTemplate(@Multipart(value = FILE, required = true) DataHandler data, @Multipart(value = "config", required = true) WsEtlTemplateData config) {
        List<Card> list = (List) inlineService.importDataInline(toDataSource(data), config.toInlineModel(), config.toInlineTemplate());
        return response(list(list).map(helper::serializeCard));
    }

    private Object getMany(String viewMode, List<EtlTemplate> templates, WsQueryOptions wsQueryOptions) {
        List<Map<String, Object>> list = list(templates)
                .withOnly(isAdminViewMode(viewMode) ? Predicates.alwaysTrue() : EtlTemplate::isActive)
                .map(wsQueryOptions.isDetailed() ? EtlTemplateWs::serializeDetailedTemplate : EtlTemplateWs::serializeBasicTemplate)
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter()));
        return response(paged(list, wsQueryOptions.getQuery()));
    }

    public static FluentMap serializeBasicTemplate(EtlTemplate template) {
        return map(
                "_id", template.getCode(),
                "code", template.getCode(),
                "description", template.getDescription(),
                "targetType", serializeEnum(template.getTargetType()),
                "targetName", template.getTargetName(),
                "active", template.isActive(),
                "type", serializeEnum(template.getType()),
                "_export", template.isExportTemplate(),
                "_import", template.isImportTemplate()
        );
    }

    public static FluentMap serializeDetailedTemplate(EtlTemplate template) {
        return serializeBasicTemplate(template).with(
                "fileFormat", serializeEnum(template.getFileFormat()),
                "errorTemplate", template.getErrorTemplate(),
                "notificationTemplate", template.getNotificationTemplate(),
                "errorAccount", template.getErrorAccount(),
                "notificationAccount", template.getNotificationAccount(),
                "exportFilter", serializeFilter(template.getExportFilter()),//TODO improve this, see below
                "importFilter", serializeFilter(template.getImportFilter()),
                "filter", fromJson(serializeFilter(template.getFilter()), JsonNode.class),
                "mergeMode", serializeEnum(template.getMergeMode()),
                "mergeMode_when_missing_update_attr", template.getAttributeNameForUpdateAttrOnMissing(),
                "mergeMode_when_missing_update_value", template.getAttributeValueForUpdateAttrOnMissing(),
                "csv_separator", template.getCsvSeparator(),
                "importKeyAttributes", template.getImportKeyAttributes(),
                "handleMissingRecordsOnError", template.getHandleMissingRecordsOnError(),
                "useHeader", template.getUseHeader(),
                "ignoreColumnOrder", template.getIgnoreColumnOrder(),
                "headerRow", template.getHeaderRow(),
                "dataRow", template.getDataRow(),
                "firstCol", template.getFirstCol(),
                "source", template.getSource(),
                "charset", template.getCharset(),
                "decimalSeparator", template.getDecimalSeparator(),
                "dateFormat", template.getDateFormat(),
                "timeFormat", template.getTimeFormat(),
                "dateTimeFormat", template.getDateTimeFormat(),
                "thousandsSeparator", template.getThousandsSeparator(),
                "enableCreate", serializeEnum(template.getEnableCreate()),
                "columns", template.getColumns().stream().map(c -> map(
                "attribute", c.getAttributeName(),
                "columnName", c.getColumnName(),
                "default", c.getDefault(),
                "mode", serializeEnum(c.getMode())
        ).skipNullValues().with(
                "decimalSeparator", c.getDecimalSeparator(),
                "dateFormat", c.getDateFormat(),
                "timeFormat", c.getTimeFormat(),
                "dateTimeFormat", c.getDateTimeFormat(),
                "thousandsSeparator", c.getThousandsSeparator()
        )).collect(toList()));
    }

    public static class WsEtlTemplateData {

        private final String code, description, targetName, exportFilter, importFilter, mergeModeUpdateAttr, mergeModeUpdateValue, csvSeparator, source, charset, errorTemplate, errorAccount, notificationTemplate, notificationAccount;
        private final List<String> importKeyAttributes;
        private final Boolean active;
        private final EtlTemplateType type;
        private final EtlMergeMode mergeMode;
        private final EtlTemplateTarget targetType;
        private final EtlFileFormat fileFormat;
        private final List<WsEtlColumnData> columns;
        private final Boolean useHeader, ignoreColumnOrder, handleMissingRecordsOnError;
        private final EnableCreate enableCreate;
        private final Integer headerRow, dataRow, firstCol;
        private final JsonNode filter;
        private final List<WsAttributeData> attributes;
        private final String dateFormat, timeFormat, decimalSeparator, dateTimeFormat, thousandsSeparator;

        public WsEtlTemplateData(
                @JsonProperty("errorTemplate") String errorTemplate,
                @JsonProperty("notificationTemplate") String notificationTemplate,
                @JsonProperty("errorAccount") String errorAccount,
                @JsonProperty("notificationAccount") String notificationAccount,
                @JsonProperty("fileFormat") String fileFormat,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("targetName") String targetName,
                @JsonProperty("targetType") String targetType,
                @JsonProperty("source") String source,
                @JsonProperty("exportFilter") String exportFilter,
                @JsonProperty("importFilter") String importFilter,
                @JsonProperty("mergeMode") String mergeMode,
                @JsonProperty("mergeMode_when_missing_update_attr") String mergeModeUpdateAttr,
                @JsonProperty("mergeMode_when_missing_update_value") String mergeModeUpdateValue,
                @JsonProperty("active") Boolean active,
                @JsonProperty("enableCreate") String enableCreate,
                @JsonProperty("type") String type,
                @JsonProperty("useHeader") Boolean useHeader,
                @JsonProperty("ignoreColumnOrder") Boolean ignoreColumnOrder,
                @JsonProperty("headerRow") Integer headerRow,
                @JsonProperty("dataRow") Integer dataRow,
                @JsonProperty("firstCol") Integer firstCol,
                @JsonProperty("charset") String charset,
                @JsonProperty("csv_separator") String csvSeparator,
                @JsonProperty("importKeyAttributes") Object importKeyAttributes,
                @JsonProperty("filter") JsonNode filter,
                @JsonProperty("columns") List<WsEtlColumnData> columns,
                @JsonProperty("dateFormat") String dateFormat,
                @JsonProperty("timeFormat") String timeFormat,
                @JsonProperty("decimalSeparator") String decimalSeparator,
                @JsonProperty("dateTimeFormat") String dateTimeFormat,
                @JsonProperty("thousandsSeparator") String thousandsSeparator,
                @JsonProperty("handleMissingRecordsOnError") Boolean handleMissingRecordsOnError,
                @JsonProperty("attributes") List<WsAttributeData> attributes) {
            this.errorTemplate = errorTemplate;
            this.notificationTemplate = notificationTemplate;
            this.errorAccount = errorAccount;
            this.code = code;
            this.description = description;
            this.targetName = targetName;
            this.targetType = parseEnumOrNull(targetType, EtlTemplateTarget.class);
            this.exportFilter = exportFilter;
            this.importFilter = importFilter;
            this.mergeModeUpdateAttr = mergeModeUpdateAttr;
            this.mergeModeUpdateValue = mergeModeUpdateValue;
            this.source = source;
            this.active = active;
            this.type = parseEnumOrNull(type, EtlTemplateType.class);
            this.mergeMode = parseEnumOrNull(mergeMode, EtlMergeMode.class);
            this.columns = firstNotNull(columns, emptyList());
            this.fileFormat = parseEnumOrNull(fileFormat, EtlFileFormat.class);
            this.csvSeparator = csvSeparator;
            this.notificationAccount = notificationAccount;
            this.importKeyAttributes = convert(importKeyAttributes, List.class);
            this.useHeader = useHeader;
            this.headerRow = headerRow;
            this.dataRow = dataRow;
            this.firstCol = firstCol;
            this.ignoreColumnOrder = ignoreColumnOrder;
            this.filter = filter;
            this.charset = charset;
            this.attributes = ImmutableList.copyOf(firstNotNull(attributes, emptyList()));
            this.dateFormat = dateFormat;
            this.timeFormat = timeFormat;
            this.decimalSeparator = decimalSeparator;
            this.thousandsSeparator = thousandsSeparator;
            this.dateTimeFormat = dateTimeFormat;
            this.handleMissingRecordsOnError = handleMissingRecordsOnError;
            this.enableCreate = parseEnumOrNull(enableCreate, EnableCreate.class);
        }

        public Classe toInlineModel() {
            return ClasseImpl.builder().withName("Model").withAttributes(list(attributes).map(WsAttributeData::toAttrDefinition)).build();
        }

        public EtlTemplate toInlineTemplate() {
            return EtlTemplateImpl.builder()
                    .withCode("Template")
                    .withConfig(b -> b
                    .withColumns(list(attributes).map(WsAttributeData::toAttrDefinition).map(a -> EtlTemplateColumnConfigImpl.builder().withAttributeName(a.getName()).withColumnName(a.getName()).accept(c -> {
                switch (a.getType().getName()) {
                    case REFERENCE, FOREIGNKEY, LOOKUP ->
                        c.withMode(ETCM_ID);
                }
            }).build()))
                    .withType(ETT_IMPORT_EXPORT)
                    .withTargetType(ET_CLASS)
                    .withTargetName("Model")
                    .withFileFormat(fileFormat)
                    .withCsvSeparator(csvSeparator)
                    .withCharset(charset)
                    .withMergeMode(EM_NO_MERGE)
                    .withIgnoreColumnOrder(firstNotNull(ignoreColumnOrder, true))
                    .withUseHeader(firstNotNull(useHeader, true)))
                    .build();
        }

        public EtlTemplateImplBuilder toImportExportTemplate() {
            return EtlTemplateImpl.builder()
                    .withCode(code)
                    .withDescription(description)
                    .withActive(active).withConfig(c -> c
                    .withAttributeNameForUpdateAttrOnMissing(mergeModeUpdateAttr)
                    .withAttributeValueForUpdateAttrOnMissing(mergeModeUpdateValue)
                    .withErrorAccount(errorAccount)
                    .withErrorTemplate(errorTemplate)
                    .withNotificationTemplate(notificationTemplate)
                    .withNotificationAccount(notificationAccount)
                    .withMergeMode(mergeMode)
                    .withTargetName(targetName)
                    .withTargetType(targetType)
                    .withType(type)
                    .withFileFormat(fileFormat)
                    .withCsvSeparator(csvSeparator)
                    .withImportKeyAttributes(importKeyAttributes)
                    .withUseHeader(useHeader)
                    .withIgnoreColumnOrder(ignoreColumnOrder)
                    .withHeaderRow(headerRow)
                    .withDataRow(dataRow)
                    .withFirstCol(firstCol)
                    .withCharset(charset)
                    .withSource(source)
                    .withFilterAsString(filter == null ? null : toJson(filter))
                    .withExportFilterAsString(exportFilter)
                    .withImportFilterAsString(importFilter)
                    .withTimeFormat(timeFormat)
                    .withDateFormat(dateFormat)
                    .withDecimalSeparator(decimalSeparator)
                    .withDateTimeFormat(dateTimeFormat)
                    .withThousandsSeparator(thousandsSeparator)
                    .withHandleMissingRecordsOnError(handleMissingRecordsOnError)
                    .withEnableCreate(enableCreate)
                    .withColumns(columns.stream().map(WsEtlColumnData::toColumnConfig).collect(toImmutableList())));
        }
    }

    public static class WsEtlColumnData {

        private final String attribute, columnName, defaultValue, dateFormat, timeFormat, decimalSeparator, dateTimeFormat, thousandsSeparator;
        private final EtlTemplateColumnMode mode;

        public WsEtlColumnData(
                @JsonProperty("attribute") String attribute,
                @JsonProperty("columnName") String columnName,
                @JsonProperty("default") String defaultValue,
                @JsonProperty("dateFormat") String dateFormat,
                @JsonProperty("timeFormat") String timeFormat,
                @JsonProperty("decimalSeparator") String decimalSeparator,
                @JsonProperty("dateTimeFormat") String dateTimeFormat,
                @JsonProperty("thousandsSeparator") String thousandsSeparator,
                @JsonProperty("mode") String mode) {
            this.attribute = attribute;
            this.columnName = columnName;
            this.defaultValue = defaultValue;
            this.dateFormat = dateFormat;
            this.timeFormat = timeFormat;
            this.decimalSeparator = decimalSeparator;
            this.thousandsSeparator = thousandsSeparator;
            this.dateTimeFormat = dateTimeFormat;
            this.mode = parseEnumOrNull(mode, EtlTemplateColumnMode.class);
        }

        public EtlTemplateColumnConfig toColumnConfig() {
            return EtlTemplateColumnConfigImpl.builder()
                    .withAttributeName(attribute)
                    .withColumnName(columnName)
                    .withDefault(defaultValue)
                    .withMode(mode)
                    .withTimeFormat(timeFormat)
                    .withDateFormat(dateFormat)
                    .withDecimalSeparator(decimalSeparator)
                    .withDateTimeFormat(dateTimeFormat)
                    .withThousandsSeparator(thousandsSeparator)
                    .build();
        }
    }
}
