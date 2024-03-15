package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.AttrEditorType;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType;
import org.cmdbuild.dao.entrytype.AttributeMetadata.MobileEditor;
import org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteaArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FloatAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FormulaAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.GeometryAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntervalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocation;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService.TYPE_NAME_MAP;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/attributes/")
@Produces(APPLICATION_JSON)

public class ClassAttributeWs {

    private final UserClassService classService;
    private final AttributeTypeConversionService conversionService;

    public ClassAttributeWs(UserClassService classService, AttributeTypeConversionService conversionService) {
        this.classService = checkNotNull(classService);
        this.conversionService = checkNotNull(conversionService);
    }

    @GET
    @Path("{attrId}/")
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        Attribute attribute = classService.getUserAttribute(classId, attrId);
        return response(conversionService.serializeAttributeType(attribute));
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<Attribute> attributeList = isAdminViewMode(viewMode) ? classService.getUserAttributes(classId) : classService.getActiveUserAttributes(classId);
        List list = attributeList.stream().map(a -> conversionService.serializeAttributeType(a, isAdminViewMode(viewMode))).collect(toList());
        return response(paged(list, offset, limit));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
        return response(conversionService.serializeAttributeType(attribute));
    }

    @PUT
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
        return response(conversionService.serializeAttributeType(attribute));
    }

    @DELETE
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        classService.deleteAttribute(classId, attrId);
        return success();
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object reorder(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Classe classe = classService.getUserClass(classId);
        classService.updateAttributes(prepareAttributesToUpdateForOrder(classe::getAttribute, attrOrder));
        classe = classService.getUserClass(classId);
        return response(attrOrder.stream().map(classe::getAttribute).map(conversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }

    public static List<Attribute> prepareAttributesToUpdateForOrder(Function<String, Attribute> attributeFun, List<String> attributes) {
        checkArgument(set(attributes).size() == attributes.size());
        List<Attribute> list = list();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = checkNotNull(attributeFun.apply(attributes.get(i)));
            int newIndex = i + 1;
            if (attribute.getIndex() != newIndex) {
                list.add(AttributeImpl.copyOf(attribute).withIndex(newIndex).build());
            }
        }
        return list;
    }

    public static class WsAttributeData {

        private final AttributeTypeName typeName;
        private final String name, formatPattern, unitOfMeasure, mobileEditorRegex;
        private final MobileEditor mobileEditor;
        private final UnitOfMeasureLocation unitOfMeasureLocation;
        private final Boolean password, showPreview, preselectIfUnique, showSeparators, domainKey, showSeconds, showThousandsSeparator, isMasterDetail, unique, mandatory, active, showInGrid, showInReducedGrid, showLabel, labelRequired, helpAlwaysVisible, hideInFilter, hideInGrid, isSortable;
        private final String formulaCode, dmsCategory, domain;
        private final String syncToDmsAttr, description, masterDetailDescription;
        private final Integer index, visibleDecimals;
        private final String defaultValue;
        private final String group;
        private final Integer precision;
        private final Integer scale;
        private final String targetClass;
        private final Integer maxLength;
        private final AttrEditorType editorType;
        private final String lookupType;
        private final String filter;
        private final Boolean useDomainFilter;
        private final AttributePermissionMode mode;
        private final Map<String, String> metadata;
        private final Integer classOrder;
        private final String ipType, help, showIf, validationRules, autoValue;
        private final RelationDirection direction;
        private final TextContentSecurity textContentSecurity;
        private final FormulaType formulaType;
        private final ShowPassword showPassword;

        public WsAttributeData(
                @JsonProperty("formatPattern") String formatPattern,
                @JsonProperty("unitOfMeasure") String unitOfMeasure,
                @JsonProperty("unitOfMeasureLocation") String unitOfMeasureLocation,
                @JsonProperty("visibleDecimals") Integer visibleDecimals,
                @JsonProperty("preselectIfUnique") Boolean preselectIfUnique,
                @JsonProperty("showThousandsSeparator") Boolean showThousandsSeparator,
                @JsonProperty("showSeconds") Boolean showSeconds,
                @JsonProperty("showSeparators") Boolean showSeparators,
                @JsonProperty("type") String type,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("showInGrid") Boolean showInGrid,
                @JsonProperty("showInReducedGrid") Boolean showInReducedGrid,
                @JsonProperty("domainKey") Boolean domainKey,
                @JsonProperty("domain") String domain,
                @JsonProperty("direction") String direction,
                @JsonProperty("unique") Boolean unique,
                @JsonProperty("mandatory") Boolean mandatory,
                @JsonProperty("active") Boolean active,
                @JsonProperty("index") Integer index,
                @JsonProperty("defaultValue") String defaultValue,
                @JsonProperty("group") String group,
                @JsonProperty("precision") Integer precision,
                @JsonProperty("scale") Integer scale,
                @JsonProperty("targetClass") String targetClass,
                @JsonProperty("maxLength") Integer maxLength,
                @JsonProperty("editorType") String editorType,
                @JsonProperty("lookupType") String lookupType,
                @JsonProperty("filter") String filter,
                @JsonProperty("useDomainFilter") Boolean useDomainFilter,
                @JsonProperty("help") String help,
                @JsonProperty("showIf") String showIf,
                @JsonProperty("showLabel") Boolean showLabel,
                @JsonProperty("labelRequired") Boolean labelRequired,
                @JsonProperty("showPreview") Boolean showPreview,
                @JsonProperty("validationRules") String validationRules,
                @JsonProperty("autoValue") String autoValue,
                @JsonProperty("dmsCategory") String dmsCategory,
                @JsonProperty("formulaType") String formulaType,
                @JsonProperty("password") Boolean password,
                @JsonProperty("helpAlwaysVisible") Boolean helpAlwaysVisible,
                @JsonProperty("showPassword") String showPassword,
                @JsonProperty("formulaCode") String formulaCode,
                @JsonProperty("mode") String mode,
                @JsonProperty("metadata") Map<String, String> metadata,
                @JsonProperty("classOrder") Integer classOrder,
                @JsonProperty("isMasterDetail") Boolean isMasterDetail,
                @JsonProperty("masterDetailDescription") String masterDetailDescription,
                @JsonProperty("ipType") String ipType,
                @JsonProperty("textContentSecurity") String textContentSecurity,
                @JsonProperty("syncToDmsAttr") String syncToDmsAttr,
                @JsonProperty("mobileEditorRegex") String mobileEditorRegex,
                @JsonProperty("mobileEditor") String mobileEditor,
                @JsonProperty("hideInFilter") Boolean hideInFilter,
                @JsonProperty("hideInGrid") Boolean hideInGrid,
                @JsonProperty("sortingEnabled") Boolean isSortable) {
            this.typeName = checkNotNull(map(TYPE_NAME_MAP.inverse()).mapKeys(s -> s.toLowerCase()).get(checkNotBlank(type, "attr type cannote be null").toLowerCase()), "unknown attr type = %s", type);
            this.name = checkNotBlank(name, "attr name cannot be null");
            this.description = description;
            this.showInGrid = showInGrid;
            this.showInReducedGrid = showInReducedGrid;
            this.unique = unique;
            this.mandatory = mandatory;
            this.active = active;
            this.index = index;
            this.defaultValue = defaultValue;
            this.domainKey = domainKey;
            this.group = trimToNull(group);
            this.precision = precision;
            this.scale = scale;
            this.maxLength = maxLength;
            this.editorType = parseEnumOrNull(editorType, AttrEditorType.class);
            this.lookupType = lookupType;
            this.filter = filter;
            this.useDomainFilter = useDomainFilter;
            this.showSeconds = showSeconds;
            this.showThousandsSeparator = showThousandsSeparator;
            this.mode = parseEnumOrNull(mode, AttributePermissionMode.class);
            this.metadata = metadata == null ? emptyMap() : map(metadata).immutable();
            this.classOrder = classOrder;
            this.ipType = ipType;
            this.help = help;
            this.showIf = showIf;
            this.helpAlwaysVisible = helpAlwaysVisible;
            this.labelRequired = labelRequired;
            this.showLabel = showLabel;
            this.dmsCategory = dmsCategory;
            this.showPreview = showPreview;
            this.formulaCode = formulaCode;
            this.formulaType = parseEnumOrNull(formulaType, FormulaType.class);
            this.password = password;
            this.showPassword = parseEnumOrNull(showPassword, ShowPassword.class);
            this.validationRules = validationRules;
            this.autoValue = autoValue;
            this.isMasterDetail = isMasterDetail;
            this.masterDetailDescription = masterDetailDescription;
            this.hideInFilter = hideInFilter;
            this.hideInGrid = hideInGrid;
            this.isSortable = isSortable;

            this.formatPattern = formatPattern;
            this.preselectIfUnique = preselectIfUnique;
            this.showSeparators = showSeparators;
            this.unitOfMeasure = unitOfMeasure;
            this.unitOfMeasureLocation = parseEnumOrNull(unitOfMeasureLocation, UnitOfMeasureLocation.class);
            this.visibleDecimals = visibleDecimals;
            this.textContentSecurity = parseEnumOrNull(textContentSecurity, TextContentSecurity.class);
            this.syncToDmsAttr = syncToDmsAttr;

            this.mobileEditor = parseEnumOrNull(mobileEditor, MobileEditor.class);
            this.mobileEditorRegex = mobileEditorRegex;

            switch (typeName) {
                case REFERENCE, REFERENCEARRAY -> {
                    this.direction = parseEnum(checkNotBlank(direction, "domain direction cannot be null"), RelationDirection.class);
                    this.domain = checkNotBlank(domain, "domain cannot be null");
                    this.targetClass = null;
                }
                case FOREIGNKEY -> {
                    this.direction = null;
                    this.domain = null;
                    this.targetClass = checkNotBlank(targetClass, "target class cannot be null");
                }
                default -> {
                    this.direction = null;
                    this.domain = null;
                    this.targetClass = null;
                }
            }
        }

        public String getName() {
            return name;
        }

        public Attribute toAttrDefinition(EntryType owner) {
            return AttributeImpl.copyOf(toAttrDefinition()).withOwner(owner).build();
        }

        public AttributeWithoutOwner toAttrDefinition() {
            return AttributeWithoutOwnerImpl.builder()
                    .withName(name)
                    .withType(getAttrType())
                    .withMeta(AttributeMetadataImpl.builder()
                            .withMetadata(metadata)
                            .withActive(active)
                            .withClassOrder(classOrder)
                            .withDefaultValue(defaultValue)
                            .withDescription(description)
                            .withEditorType(editorType)
                            .withFilter(filter)//TODO check this
                            .withUseDomainFilter(useDomainFilter)
                            .withGroup(group)
                            .withIndex(index)
                            .withMode(mode)
                            .withRequired(mandatory)
                            .withShowInGrid(showInGrid)
                            .withShowInReducedGrid(showInReducedGrid)
                            .withTargetClass(targetClass)
                            .withUnique(unique)
                            .withFormatPattern(formatPattern)
                            .withPreselectIfUnique(preselectIfUnique)
                            .withUnitOfMeasure(unitOfMeasure)
                            .withUnitOfMeasureLocation(unitOfMeasureLocation)
                            .withVisibleDecimal(visibleDecimals)
                            .withShowSeparators(showSeparators)
                            .withValidationRulesExpr(validationRules)
                            .withAutoValueExpr(autoValue)
                            .withShowIfExpr(showIf)
                            .withHelpMessage(help)
                            .withDomainKey(domainKey)
                            .withShowThousandsSeparators(showThousandsSeparator)
                            .withShowSeconds(showSeconds)
                            .withMasterDetail(isMasterDetail)
                            .withMasterDetailDescription(masterDetailDescription)
                            .withTextContentSecurity(textContentSecurity)
                            .withMaxLength(maxLength)
                            .withShowLabel(showLabel)
                            .withLabelRequired(labelRequired)
                            .withHelpAlwaysVisible(helpAlwaysVisible)
                            .withDmsCategory(dmsCategory)
                            .withShowPreview(showPreview)
                            .withFormulaType(formulaType)
                            .withFormulaCode(formulaCode)
                            .withShowPassword(showPassword)
                            .withPassword(password)
                            .witSyncToDmsAttr(syncToDmsAttr)
                            .withHideInFilter(hideInFilter)
                            .withHideInGrid(hideInGrid)
                            .withMobileEditor(mobileEditor)
                            .withMobileEditorRegex(mobileEditorRegex)
                            .withSortable(isSortable)
                            .build())
                    .build();
        }

        private CardAttributeType getAttrType() {
            return switch (typeName) {
                case DECIMAL ->
                    new DecimalAttributeType(precision, scale);
                case FOREIGNKEY ->
                    new ForeignKeyAttributeType(targetClass);
                case INET ->
                    new IpAddressAttributeType(parseEnumOrDefault(ipType, IpType.IPV4));
                case LOOKUP ->
                    new LookupAttributeType(lookupType);
                case LOOKUPARRAY ->
                    new LookupArrayAttributeType(lookupType);
                case REFERENCE ->
                    new ReferenceAttributeType(domain, direction);
                case STRING ->
                    new StringAttributeType(maxLength);
                case BOOLEAN ->
                    new BooleanAttributeType();
                case BYTEARRAY ->
                    new ByteArrayAttributeType();
                case CHAR ->
                    new CharAttributeType();
                case DATE ->
                    new DateAttributeType();
                case DOUBLE ->
                    new DoubleAttributeType();
                case FLOAT ->
                    FloatAttributeType.INSTANCE;
                case FORMULA ->
                    FormulaAttributeType.INSTANCE;
                case FILE ->
                    FileAttributeType.INSTANCE;
                case LINK ->
                    LinkAttributeType.INSTANCE;
                case REGCLASS ->
                    RegclassAttributeType.INSTANCE;
                case INTEGER ->
                    new IntegerAttributeType();
                case LONG ->
                    LongAttributeType.INSTANCE;
                case GEOMETRY ->
                    GeometryAttributeType.INSTANCE;
                case JSON ->
                    JsonAttributeType.INSTANCE;
                case STRINGARRAY ->
                    new StringArrayAttributeType();
                case BYTEAARRAY ->
                    new ByteaArrayAttributeType();
                case TEXT ->
                    new TextAttributeType();
                case TIME ->
                    new TimeAttributeType();
                case TIMESTAMP ->
                    new DateTimeAttributeType();
                case INTERVAL ->
                    IntervalAttributeType.INSTANCE;
                default ->
                    throw new UnsupportedOperationException("unsupported attribute type = " + name);
            };
        }
    }
}
