/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeAttributePermissionMode;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializePermissions;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import static org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocationUtils.serializeUnitOfMeasureLocation;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.EcqlRepository;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.ecql.utils.EcqlUtils.buildDomainEcqlId;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlBindingInfoForExpr;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpression;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromAttributeFilter;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_BOOLEAN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_BYTEA_ARRAY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_CHAR;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DATE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DATE_TIME;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DECIMAL;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DOUBLE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_ENTRY_TYPE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FILE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FLOAT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FOREIGN_KEY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FORMULA;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_GEOMETRY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_INTEGER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_IP_ADDRESS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LINK;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LONG;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LOOKUP;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LOOKUP_ARRAY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_REFERENCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_STRING;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_STRING_ARRAY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_TEXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_TIME;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypeConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final BiMap<AttributeTypeName, String> TYPE_NAME_MAP = ImmutableBiMap.copyOf(map(
            AttributeTypeName.BOOLEAN, TYPE_BOOLEAN,
            AttributeTypeName.CHAR, TYPE_CHAR,
            AttributeTypeName.DATE, TYPE_DATE,
            AttributeTypeName.TIMESTAMP, TYPE_DATE_TIME,
            AttributeTypeName.DECIMAL, TYPE_DECIMAL,
            AttributeTypeName.DOUBLE, TYPE_DOUBLE,
            AttributeTypeName.FLOAT, TYPE_FLOAT,
            AttributeTypeName.REGCLASS, TYPE_ENTRY_TYPE,
            AttributeTypeName.FOREIGNKEY, TYPE_FOREIGN_KEY,
            AttributeTypeName.INTEGER, TYPE_INTEGER,
            AttributeTypeName.LONG, TYPE_LONG,
            AttributeTypeName.LINK, TYPE_LINK,
            AttributeTypeName.FORMULA, TYPE_FORMULA,
            AttributeTypeName.FILE, TYPE_FILE,
            AttributeTypeName.LOOKUPARRAY, TYPE_LOOKUP_ARRAY,
            AttributeTypeName.INET, TYPE_IP_ADDRESS,
            AttributeTypeName.LOOKUP, TYPE_LOOKUP,
            AttributeTypeName.REFERENCE, TYPE_REFERENCE,
            AttributeTypeName.STRINGARRAY, TYPE_STRING_ARRAY,
            AttributeTypeName.BYTEAARRAY, TYPE_BYTEA_ARRAY,
            AttributeTypeName.STRING, TYPE_STRING,
            AttributeTypeName.GEOMETRY, TYPE_GEOMETRY,
            AttributeTypeName.JSON, TYPE_JSON,
            AttributeTypeName.TEXT, TYPE_TEXT,
            AttributeTypeName.TIME, TYPE_TIME));

    private final DaoService dao;
    private final ObjectTranslationService translationService;
    private final UserClassService classService;
    private final UserDomainService userDomainService;
    private final CalendarWsSerializationHelper calendarHelper;
    private final LookupService lookupService;
    private final DmsService dmsService;
    private final EcqlRepository ecqlRepository;

    public AttributeTypeConversionService(
            DaoService dao,
            ObjectTranslationService translationService,
            UserClassService classService,
            UserDomainService userDomainService,
            CalendarWsSerializationHelper calendarHelper,
            LookupService lookupService,
            DmsService dmsService,
            EcqlRepository ecqlRepository) {
        this.dao = checkNotNull(dao);
        this.translationService = checkNotNull(translationService);
        this.classService = checkNotNull(classService);
        this.userDomainService = checkNotNull(userDomainService);
        this.calendarHelper = checkNotNull(calendarHelper);
        this.lookupService = checkNotNull(lookupService);
        this.dmsService = checkNotNull(dmsService);
        this.ecqlRepository = ecqlRepository;
    }

    public static String serializeAttributeType(AttributeTypeName name) {
        return checkNotNull(TYPE_NAME_MAP.get(name), "unsupported attr type = %s", name);
    }

    public FluentMap<String, Object> serializeAttributeType(Attribute attribute) {
        return serializeAttributeType(attribute, true);
    }

    public FluentMap<String, Object> serializeAttributeType(Attribute attribute, boolean showNoActive) {
        return new AttrSerializerHelper(attribute, showNoActive).serializeAttributeType();
    }

    private class AttrSerializerHelper {

        private final Attribute attribute;
        private final boolean showNoActive;

        public AttrSerializerHelper(Attribute attribute, boolean showNoActive) {
            this.attribute = attribute;
            this.showNoActive = showNoActive;
        }

        public FluentMap<String, Object> serializeAttributeType() {
            return (FluentMap) map(
                    "_id", attribute.getName(),
                    "type", AttributeTypeConversionService.serializeAttributeType(attribute.getType().getName()),
                    "name", attribute.getName(),
                    "description", attribute.getDescription(),
                    "_description_translation", translationService.translateAttributeDescription(attribute),
                    "showInGrid", attribute.showInGrid(),
                    "showInReducedGrid", attribute.showInReducedGrid(),
                    "unique", attribute.isUnique(),
                    "mandatory", attribute.isMandatory(),
                    "inherited", attribute.isInherited(),
                    "active", attribute.isActive(),
                    "index", attribute.getIndex(),
                    "defaultValue", attribute.getDefaultValue(),
                    "group", attribute.getGroupNameOrNull(),
                    "_group_description", attribute.getGroupDescriptionOrNull(),
                    "_group_description_translation", attribute.hasGroup() ? (attribute.getOwner().isView() ? translationService.translateViewAttributeGroupDescription(attribute.getOwner().getName(), attribute.getGroupName(), attribute.getGroupDescriptionOrNull()) : translationService.translateAttributeGroupDescription(attribute.getOwner(), attribute.getGroup())) : null,
                    "mode", serializeAttributePermissionMode(attribute.getMode()),
                    "writable", attribute.hasUiPermission(AP_CREATE) || attribute.hasUiPermission(AP_UPDATE),
                    "immutable", attribute.hasUiPermission(AP_CREATE) && !attribute.hasUiPermission(AP_UPDATE),
                    "hidden", !attribute.hasUiReadPermission(),
                    "_can_read", attribute.hasServiceReadPermission(),
                    "_can_create", attribute.hasServicePermission(AP_CREATE),
                    "_can_update", attribute.hasServicePermission(AP_UPDATE),
                    "_can_modify", attribute.hasServicePermission(AP_MODIFY),
                    "metadata", attribute.getMetadata().getCustomMetadata(),
                    "help", attribute.getMetadata().getHelpMessage(),
                    "showIf", attribute.getMetadata().getShowIfExpr(),
                    "validationRules", attribute.getMetadata().getValidationRulesExpr(),
                    "autoValue", attribute.getMetadata().getAutoValueExpr(),
                    "alias", attribute.getMetadata().getUiAlias(),
                    "syncToDmsAttr", attribute.getMetadata().getSyncToDmsAttr(),
                    "helpAlwaysVisible", attribute.getMetadata().helpAlwaysVisible(),
                    "hideInFilter", attribute.isHiddenInFilter(),
                    "hideInGrid", attribute.isHiddenInGrid(),
                    "virtual", attribute.isVirtual(),
                    "sortingEnabled", attribute.isSortable())
                    .accept((m) -> {
                        if (attribute.hasServiceModifyPermission()) {
                            m.put("_permissions", serializePermissions(attribute));
                        }
                        if (attribute.getOwner().isDomain()) {
                            m.put("domainKey", attribute.isDomainKey());
                        }
                    })
                    .with(serializeAttributeSpecificValues(attribute));
        } // end serializeAttributeType method

        private FluentMap<String, Object> serializeAttributeSpecificValues(Attribute attribute) {
            FluentMap<String, Object> map = map();
            attribute.getType().accept(new NullAttributeTypeVisitor() {
                @Override
                public void visit(DecimalAttributeType attributeType) {
                    map.put("precision", attributeType.getPrecision(), "scale", attributeType.getScale());
                }

                @Override
                public void visit(ForeignKeyAttributeType attributeType) {
                    Classe classe = dao.getClasse(attributeType.getForeignKeyDestinationClassName());
                    map.put("targetClass", classe.getName(),
                            "targetType", getType(classe),
                            "cascadeAction", serializeEnum(attributeType.getForeignKeyCascadeAction()),
                            "filter", attribute.getFilter(),
                            "isMasterDetail", attribute.getMetadata().isMasterDetail(),
                            "masterDetailDescription", attribute.getMetadata().getMasterDetailDescription());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(LookupAttributeType attributeType) {
                    map.put("lookupType", attributeType.getLookupTypeName(),
                            "filter", attribute.getFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(LookupArrayAttributeType attributeType) {
                    map.put("lookupType", attributeType.getLookupTypeName(),
                            "filter", attribute.getFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(ReferenceAttributeType attributeType) {
                    Domain domain = userDomainService.getDomain(attributeType.getDomainName());
                    Classe target = domain.getReferencedClass(attributeType);
                    map.put("domain", domain.getName(),
                            "direction", serializeRelationDirection(attributeType.getDirection()),
                            "targetClass", target.getName(),
                            "targetType", getType(target),
                            "filter", attribute.getFilter(),
                            "useDomainFilter", attribute.getMetadata().isUseDomainFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute, domain);
                }

                @Override
                public void visit(TextAttributeType attributeType) {
                    map.put("language", serializeEnum(attributeType.getLanguage()),
                            "editorType", serializeEnumUpper(attribute.getEditorType()));
                }

                @Override
                public void visit(IpAddressAttributeType attributeType) {
                    map.put("ipType", attributeType.getType().name().toLowerCase());
                }
            });
            switch (attribute.getType().getName()) {
                case REFERENCE, FOREIGNKEY, LOOKUP, LOOKUPARRAY ->
                    map.put("preselectIfUnique", attribute.getMetadata().preselectIfUnique());
                case TIME, TIMESTAMP ->
                    map.put("showSeconds", attribute.getMetadata().showSeconds(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case DATE ->
                    map.put("formatPattern", attribute.getMetadata().getFormatPattern());
                case DECIMAL, DOUBLE, FLOAT ->
                    map.put(
                            "visibleDecimals", attribute.getMetadata().getVisibleDecimals(),
                            "unitOfMeasure", attribute.getMetadata().getUnitOfMeasure(),
                            "unitOfMeasureLocation", serializeUnitOfMeasureLocation(attribute.getMetadata().getUnitOfMeasureLocation()),
                            "showSeparators", attribute.getMetadata().showSeparators(),
                            "showThousandsSeparator", attribute.getMetadata().showThousandsSeparator(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case INTEGER, LONG ->
                    map.put(
                            "unitOfMeasure", attribute.getMetadata().getUnitOfMeasure(),
                            "unitOfMeasureLocation", serializeUnitOfMeasureLocation(attribute.getMetadata().getUnitOfMeasureLocation()),
                            "showSeparators", attribute.getMetadata().showSeparators(),
                            "showThousandsSeparator", attribute.getMetadata().showThousandsSeparator(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case STRING ->
                    map.put(
                            "password", attribute.getMetadata().isPassword(),
                            "showPassword", serializeEnum(attribute.getMetadata().getShowPassword()));
                case LINK ->
                    map.put(
                            "showLabel", attribute.getMetadata().showLabel(),
                            "labelRequired", attribute.getMetadata().labelRequired());
                case FILE -> {
                    LookupValue lookupValue = lookupService.getLookupByTypeAndCodeOrDescriptionOrId(dmsService.getCategoryLookupType(attribute.getOwnerClass()).getName(),
                            attribute.getMetadata().getDmsCategory());
                    map.put(
                            "dmsCategory", attribute.getMetadata().getDmsCategory(),
                            "dmsModel", lookupValue.getDmsModelClass(),
                            "_dmsCategory_description", lookupValue.getDescription(),
                            "showPreview", attribute.getMetadata().showPreview());
                }
                case FORMULA ->
                    map.put(
                            "formulaType", serializeEnum(attribute.getMetadata().getFormulaType()),
                            "formulaCode", attribute.getMetadata().getFormulaCode());
            }
            switch (attribute.getType().getName()) {
                case STRING, STRINGARRAY, TEXT ->
                    map.put(
                            "textContentSecurity", serializeEnum(attribute.getMetadata().getTextContentSecurity()),
                            "_html", attribute.isHtmlSafe(),
                            "maxLength", firstNotNull(attribute.getMaxLength(), Integer.MAX_VALUE),//TODO check this
                            "multiline", attribute.isMultiline());
                case LOOKUP, LOOKUPARRAY ->
                    map.put("_html", true);
                default ->
                    map.put("_html", false);
            }
            switch (attribute.getType().getName()) {
                case STRING, TEXT, DECIMAL, DOUBLE, FLOAT, INTEGER, LONG ->
                    map.put(
                            "mobileEditor", serializeEnum(attribute.getMetadata().getMobileEditor()),
                            "mobileEditorRegex", attribute.getMetadata().getMobileEditorRegex()
                    );
            }
            switch (attribute.getType().getName()) {
                case TIMESTAMP, DATE -> {
                    if (attribute.getOwner().isRegularClass()) {//TODO improve this
                        List<CalendarTriggerInfo> calendarTriggers = classService.getExtendedClass(attribute.getOwnerClass(), CQ_INCLUDE_INACTIVE_ELEMENTS).getCalendarTriggersForAttr(attribute.getName());
                        map.put("calendarTriggers", calendarTriggers.stream().filter((t) -> showNoActive || t.isActive()).map(calendarHelper::serializeDetailedTrigger).collect(toList()));
                    }
                }
            }
            return map;
        } // end serializeAttributeSpecificValues method

    } // end AttrSerializerHelper class

    private void attachEcqlFilterStuffIfApplicable(final FluentMap<String, Object> map, Attribute attribute) {
        attachEcqlFilterStuffIfApplicable(map, attribute, null);
    }

    private void attachEcqlFilterStuffIfApplicable(final FluentMap<String, Object> map, Attribute attribute, Domain domain) {
        if (attribute.isOfType(REFERENCE) && attribute.getMetadata().isUseDomainFilter()) {
            checkArgument(domain != null, "domain not found for attribute %s", attribute.getName());
            String filterSide = domain.getFilterSide(attribute);
            String filter = domain.getFilterFromFilterSide(filterSide);
            if (isNotBlank(filter)) {
                EcqlBindingInfo ecqlBindingInfo = getEcqlBindingInfoForExpr(getEcqlExpression(domain.getMetadata(), filter));
                String ecqlId = buildDomainEcqlId(domain, filterSide);
                EcqlFilterSerializationHelper.addEcqlFilter(map, "ecqlFilter", ecqlId, ecqlBindingInfo);
            }
        } else if (attribute.hasFilter()) {
            logger.debug("attaching ecql filter stuff serialization for attribute < {} >", attribute.getName());
            EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(getEcqlExpressionFromAttributeFilter(attribute));

            String ecqlId = EcqlUtils.buildAttrEcqlId(attribute);

            checkNotNull(ecqlId, format("while handling a attribute filter, unhandled attribute type %s", attribute.getOwner().getEtType()));

            EcqlFilterSerializationHelper.addEcqlFilter(map, "ecqlFilter", ecqlId, ecqlBindingInfo);
        }
    }

    private static String getType(Classe classe) {
        return classe.isProcess() ? "process" : "class";
    }

}
