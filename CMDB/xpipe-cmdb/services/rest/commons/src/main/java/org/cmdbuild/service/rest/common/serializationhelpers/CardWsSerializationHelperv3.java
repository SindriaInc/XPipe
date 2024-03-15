/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Lists.transform;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_DEFAULT;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.utils.AuthUtils;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_WRITE;
import org.cmdbuild.dao.entrytype.AttributePermissions;
import org.cmdbuild.dao.entrytype.ClassPermission;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.PermissionScope;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceAttrName;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_VERSION;
import org.cmdbuild.dms.dao.DocumentInfoRepository;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.gis.CmGeometry;
import org.cmdbuild.gis.utils.GisUtils;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelperv3.ExtendedCardOptions.SKIP_SYSTEM_ATTRS;
import static org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeConverterUtilsv3.toClient;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.serializeGeometry;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.html.HtmlSanitizerUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullNorBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardWsSerializationHelperv3 {

    private final List<String> CARD_ATTR_EXT_META = ImmutableList.of("_changed", "_previous");
    private final Set<String> SPECIAL_ATTRS = ImmutableSet.of(ATTR_IDTENANT, ATTR_IDCLASS, ATTR_ID);//TODO improve this

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final DmsService dmsService;
    private final ObjectTranslationService translationService;
    private final ClassSerializationHelper classSerializationHelper;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final WidgetService widgetService;
    private final UserClassService userClassService;
    private final UserRepository userRepository;
    private final DocumentInfoRepository repository;

    public CardWsSerializationHelperv3(DaoService dao, DmsService dmsService, ObjectTranslationService translationService, ClassSerializationHelper classSerializationHelper, AttributeTypeConversionService attributeTypeConversionService, WidgetService widgetService, UserClassService userClassService, UserRepository userRepository, DocumentInfoRepository repository) {
        this.dao = checkNotNull(dao);
        this.dmsService = checkNotNull(dmsService);
        this.translationService = checkNotNull(translationService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.widgetService = checkNotNull(widgetService);
        this.userClassService = checkNotNull(userClassService);
        this.userRepository = checkNotNull(userRepository);
        this.repository = checkNotNull(repository);
    }

    public enum ExtendedCardOptions {
        NONE, INCLUDE_MODEL, SKIP_SYSTEM_ATTRS
    }

    public FluentMap<String, Object> serializeBasicHistory(Card record) {
        return map(
                "_type", record.getType().getName(),
                "_id", record.getId(),
                "_endDate", toIsoDateTime(record.getEndDate()),
                "_beginDate", toIsoDateTime(record.getBeginDate()),
                "_user", record.getUser(),
                "__user_description", userDescription(record.getUser()),
                "_status", record.getCardStatus().name());
    }

    public Consumer<FluentMap<String, Object>> serializeWidgets(Card card) {
        return m -> {
            List<WidgetData> classWidgets = widgetService.getAllWidgetsForClass(card.getType()).stream()
                    .filter(w -> toBooleanOrDefault(card.getType().getOtherPermissions().get(format("widget_%s", w.getId())), true)).collect(toList());
            List<Widget> cardWidgets = widgetService.widgetDataToWidget(card.getClassName(), null, classWidgets, card.toMap());
            m.put("_widgets", cardWidgets.stream().map(e -> classSerializationHelper.serializeWidget(e, card.getTypeName())).collect(toList()));
        };
    }

    public FluentMap<String, Object> serializeMinimalRelation(CMRelation relation) {
        return map("_id", relation.getId(),
                "_type", relation.getType().getName(),
                "_user", relation.getUser(),
                "__user_description", userDescription(relation.getUser()),
                "_beginDate", toIsoDateTime(relation.getBeginDate()),
                "_sourceType", relation.getSourceCard().getClassName(),
                "_sourceId", relation.getSourceId(),
                "_sourceDescription", relation.getSourceDescription(),//TODO translation
                "_sourceCode", relation.getSourceCode(),
                "_destinationType", relation.getTargetCard().getClassName(),
                "_destinationId", relation.getTargetId(),
                "_destinationCode", relation.getTargetCode(),
                "_destinationDescription", relation.getTargetDescription(),//TODO translation
                "_direction", serializeRelationDirection(relation.getDirection()),
                "_is_direct", relation.isDirect(),
                "_can_update", relation.getType().hasServicePermission(CP_UPDATE),
                "_can_delete", relation.getType().hasServicePermission(CP_DELETE)
        );
    }

    @Nullable
    private String userDescription(String user) {
        return Optional.ofNullable(trimToNull(user)).map(AuthUtils::getUsernameFromHistoryUser).map(userRepository::getUserDataByUsernameOrNull).map(UserData::getDescription).orElse(user);
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedRelation(CMRelation relation) {
        return serializeMinimalRelation(relation).accept((map) -> {
            addCardValuesAndDescriptionsAndExtras(relation, map::put);
        });
    }

    public FluentMap<String, Object> serializeCard(Card card, ExtendedCardOptions... extendedCardOptions) {
        return serializeCard(card, emptyOptions(), set(extendedCardOptions));
    }

    public FluentMap<String, Object> serializeCard(Card card, DaoQueryOptions queryOptions, ExtendedCardOptions... extendedCardOptions) {
        return serializeCard(card, queryOptions, set(extendedCardOptions));

    }

    public FluentMap<String, Object> serializeCard(Card card, Set<ExtendedCardOptions> extendedCardOptions) {
        return serializeCard(card, emptyOptions(), extendedCardOptions);

    }

    public FluentMap<String, Object> serializeCard(Card card, DaoQueryOptions queryOptions, Set<ExtendedCardOptions> extendedCardOptions) {
        try {
            return mapOf(String.class, Object.class).accept((m) -> {
                if (!extendedCardOptions.contains(SKIP_SYSTEM_ATTRS)) {
                    m.put("_id", card.getId(),
                            "_type", card.getType().getName(),
                            //                            "_type_description", card.getType().getDescription(),
                            //                            "_type_description_translation", translationService.translateClassDescription(card.getType()),
                            "_user", card.getUser(),
                            "__user_description", userDescription(card.getUser()),
                            "_beginDate", toIsoDateTime(card.getBeginDate()));
                    if (card.getType().hasMultitenantEnabled()) {
                        m.put("_tenant", card.getTenantId());
                    }
                }
            }).filterKeys(k -> queryOptions.getOnlyGridAttrs() ? true : !queryOptions.hasAttrs() || queryOptions.getAttrs().contains(card.getType().getAliasToAttributeMap().getOrDefault(k, (String) k))).accept((m) -> {
                addCardValuesAndDescriptionsAndExtras(card, a -> queryOptions.getOnlyGridAttrs() ? true : !queryOptions.hasAttrs() || queryOptions.getAttrs().contains(a.getName()), m::put);
                if (extendedCardOptions.contains(INCLUDE_MODEL)) {
                    m.put("_model", classSerializationHelper.buildBasicResponse(card.getType())
                            .with("attributes", list(transform(card.getType().getServiceAttributes(), attributeTypeConversionService::serializeAttributeType))));
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error serializing card = %s", card);
        }
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card, Predicates.alwaysTrue(), adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, Predicate<Attribute> attrFilter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card.getType().getServiceAttributes().stream().filter(attrFilter).collect(toImmutableList()), card::get, adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(Collection<Attribute> attributes, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        attributes.stream()
                .filter(Attribute::isActive)
                .filter(Attribute::hasServiceReadPermission)
                .filter(a -> !SPECIAL_ATTRS.contains(a.getName()))//TODO improve this
                .forEach((a) -> {
                    addCardValuesAndDescriptionsAndExtras(a, getter, adder);
                });
    }

    public FluentMap<String, Object> serializeAttributeValue(EntryType classe, String attributeName, Object value) {
        return serializeAttributeValue(classe, attributeName, map(attributeName, value));
    }

    public FluentMap<String, Object> serializeAttributeValue(EntryType classe, String attributeName, Map<String, Object> data) {
        return serializeAttributeValue(classe.getAttribute(attributeName), data);
    }

    public FluentMap<String, Object> serializeAttributeValue(Attribute attribute, Map<String, Object> data) {
        return mapOf(String.class, Object.class).accept(m -> {
            addCardValuesAndDescriptionsAndExtras(attribute, data::get, m::put);
        });
    }

    public void addCardValuesAndDescriptionsAndExtras(Attribute attribute, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(attribute.getName(), attribute.getType(), attribute.getMetadata(), attribute, getter, adder, attribute.getOwner());
    }

    public void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType type, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(name, type, null, null, getter, adder, null);
    }

    private void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType<?> type, @Nullable AttributeMetadata attributeMetadata, @Nullable AttributePermissions attributePermission, Function<String, Object> getter, BiConsumer<String, Object> adder, EntryType attributeClass) {
        Object rawValue = "<undefined>";
        try {
            rawValue = getter.apply(name);
            Object value = toClient(type, rawValue);
            if (attributeMetadata != null && attributeMetadata.isPassword()) {
                adder.accept(format("_%s_has_value", name), isNotBlank(toStringOrNull(value)));
                switch (attributeMetadata.getShowPassword()) {
                    case SP_ALWAYS -> {
                        break; //nothing to do
                    }
                    case SP_NEVER -> {
                        return; //skip password attrs
                    }
                    case SP_ONWRITEACCESS -> {
                        if (attributePermission != null && attributeClass != null && attributePermission.hasUiPermission(AP_WRITE)) {
                            //nothing to do
                            if (attributeClass.isDomain() && ((Domain) attributeClass).hasUiPermission(ClassPermission.CP_WRITE)) {
                                break;
                            } else if (attributeClass.isClasse() && ((Classe) attributeClass).hasUiPermission(ClassPermission.CP_WRITE)) {
                                break;
                            }
                            return;
                        } else {
                            return; //skip password attrs
                        }
                    }
                    default ->
                        throw new UnsupportedOperationException();
                }
            }
            if (attributeMetadata != null && attributeMetadata.hasTextContentSecurity(TCS_HTML_SAFE)) {
                value = HtmlSanitizerUtils.sanitizeHtml(toStringOrNull(value));
            }
            adder.accept(name, value);
            switch (type.getName()) {
                case REFERENCE, FOREIGNKEY, LOOKUP -> {
                    if (type.isOfType(REFERENCE)) {
                        Domain domain = dao.getDomain(type.as(ReferenceAttributeType.class).getDomainName()).getThisDomainWithDirection(type.as(ReferenceAttributeType.class).getDirection());
                        domain.getActiveServiceAttributes().stream().filter(Attribute::showInGrid).forEach(a -> {
                            String key = buildReferenceAttrName(name, a.getName());
                            addCardValuesAndDescriptionsAndExtras(key, a.getType(), a.getMetadata(), a, getter, adder, null);
                        });
                    }
                    if (rawValue instanceof IdAndDescription idAndDescription) {
                        adder.accept(format("_%s_code", name), idAndDescription.getCode());
                        adder.accept(format("_%s_description", name), idAndDescription.getDescription());
                    }
                    if (rawValue instanceof LookupValue lookupValue) {
                        LookupValue lookup = lookupValue;
                        if (lookup.hasCode()) {
                            adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(lookup.getLookupType(), lookup.getCode(), lookup.getDescription()));
                        }
                    }
                }
                case FILE -> {
                    if (CmNullableUtils.isNotBlank(value)) {
                        DocumentInfoAndDetail document;
                        boolean isDmsServiceOk = dmsService.getService().isServiceOk();
                        if (isDmsServiceOk) {
                            document = dmsService.getCardAttachmentByMetadataId(toLong(value));
                        } else {
                            Card documentCard = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(ATTR_ID, EQ, toLong(value)).getCard();
                            String documentId = checkNotBlank(documentCard.getString(DOCUMENT_ATTR_DOCUMENTID));
                            document = repository.getById(documentId);
                            Card cardMetadata = dao.selectAll().from(documentCard.getTypeName()).whereExpr("\"DocumentId\" = ?", checkNotBlank(documentId)).getCardOrNull();
                            document = DocumentInfoAndDetailImpl.copyOf(document).withMetadata(cardMetadata).build();
                        }
                        adder.accept(name, document.getDocumentId());
                        adder.accept(format("_%s_card", name), document.getMetadata().getId());
                        map(serializeAttachment(attributeClass.getName(), document)).with("isDmsServiceOk", isDmsServiceOk).withoutKeys("_id", "_card").mapKeys(k -> format("_%s_%s", name, k)).forEach(adder);
                    }
                }
                case LOOKUPARRAY -> {
                    if (rawValue instanceof Iterable iterable) {
                        adder.accept(format("_%s_details", name), list(iterable).map(v -> map().accept(m -> {
                            if (v instanceof IdAndDescription idAndDescription) {
                                m.put("_id", idAndDescription.getId(),
                                        "code", idAndDescription.getCode(),
                                        "description", idAndDescription.getDescription());
                            }
                            if (v instanceof LookupValue && ((LookupValue) v).hasCode()) {
                                m.put("_description_translation", translationService.translateLookupDescriptionSafe(((LookupValue) v).getLookupType(), ((LookupValue) v).getCode(), ((LookupValue) v).getDescription()));
                            }
                        })));
                    }
                }
                case TEXT -> {
                    switch (((TextAttributeType) type).getLanguage()) {
                        case TAL_HTML ->
                            adder.accept(format("_%s_html", name), toStringOrEmpty(value));
                        case TAL_MARKDOWN ->
                            adder.accept(format("_%s_html", name), markdownToHtml(toStringOrEmpty(value)));
                        //TODO handle others
                    }
                }
                case GEOMETRY -> {
                    if (isNotNullNorBlank(value)) {
                        CmGeometry geometry = GisUtils.parseGeometry(toStringNotBlank(value));
                        adder.accept(format("_%s_%s", name, serializeEnum(geometry.getType())), serializeGeometry(geometry));
                    }
                }
            }
            CARD_ATTR_EXT_META.stream().map((e) -> format("_%s%s", name, e)).forEach((n) -> {
                Object v = getter.apply(n);
                if (v != null) {
                    adder.accept(n, v);
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error processing attr = %s of type = %s with value = %s", name, type, rawValue);
        }
    }

    public Map<String, Object> serializeAttachment(String classId, DocumentInfoAndDetail input) {
        return serializeAttachment(classId, input, false);
    }

    public Map<String, Object> serializeAttachment(String classId, DocumentInfoAndDetail input, boolean includeWidgets) {
        return mapOf(String.class, Object.class).accept(m -> {
            boolean canWrite = false;
            if (input.hasCategory()) {
                org.cmdbuild.lookup.LookupValue category = dmsService.getCategoryLookupForAttachment(dao.getClasse(classId), input);
                canWrite = classId.equals("Email") || classId.equals("_CalendarEvent") ? true : userClassService.getUserClass(classId).hasDmsCategoryWritePermission(formatCategoryValue(userClassService.getUserClass(classId), category.getCode()));
                if (category.isActive()) {
                    m.put(
                            "_category_name", category.getCode(),
                            "_category_description", category.getDescription(),
                            "_category_description_translation", translationService.translateLookupDescription(category.getType().getName(), category.getCode(), category.getDescription()),
                            "_can_update", classId.equals("Email") || classId.equals("_CalendarEvent") ? true : checkDmsPermission(userClassService.getUserClass(classId), category.getCode(), CP_UPDATE),
                            "_can_delete", classId.equals("Email") || classId.equals("_CalendarEvent") ? true : checkDmsPermission(userClassService.getUserClass(classId), category.getCode(), CP_DELETE)
                    );
                } else {
                    m.put(
                            "_category_name", null,
                            "_category_description", null,
                            "_category_description_translation", null,
                            "category", null,
                            "Category", null,
                            "_can_update", false,
                            "_can_delete", false);
                }
            }
            if (input.hasMetadata()) {
                Card card = input.getMetadata();
                card = CardImpl.copyOf(card).withType(ClasseImpl.copyOf(card.getType())
                        .withPermissions(ClassPermissionsImpl.copyOf(card.getType()).removePermissions(PermissionScope.PS_UI, getClassPermissionToRemove(canWrite)).build()).build()).build();
                serializeCard(card, SKIP_SYSTEM_ATTRS).withoutKeys(DOCUMENT_ATTR_DOCUMENTID, DOCUMENT_ATTR_CARD, DOCUMENT_ATTR_VERSION).forEach(m::put);//TODO check this
                m.put("_card", card.getId());
            }
        }).with(
                "_id", input.getDocumentId(),
                "name", input.getFileName(),
                "category", input.getCategory(),
                "description", input.getDescription(),
                "version", input.getVersion(),
                "author", input.getAuthor(),
                "_author_description", userDescription(input.getAuthor()),
                "created", toIsoDateTime(input.getCreated()),
                "modified", toIsoDateTime(input.getModified()))
                .accept(m -> {
                    if (includeWidgets && input.hasMetadata()) {
                        m.accept(serializeWidgets(input.getMetadata()));
                    }
                });
    }

    private Set getClassPermissionToRemove(boolean canWrite) {
        if (canWrite) {
            return set();
        } else {
            return set(CP_WRITE);
        }
    }

    private boolean checkDmsPermission(Classe userClass, String categoryValue, ClassPermission permissionToCheck) {
        Set<GrantAttributePrivilege> categoryPermission = userClass.getDmsPermissions().getOrDefault(formatCategoryValue(userClass, categoryValue), set(GAP_DEFAULT));
        if (categoryPermission.contains(GAP_WRITE)) {
            return true;
        } else if (categoryPermission.contains(GAP_DEFAULT)) {
            return userClass.isProcess() ? userClass.hasUiPermission(CP_WF_BASIC) : userClass.hasUiPermission(permissionToCheck);
        } else {
            return false;
        }
    }

    private String formatCategoryValue(Classe userClass, String categoryValue) {
        return format("%s_%s", userClass.hasDmsCategory() ? userClass.getDmsCategory() : dmsService.getDefaultDmsCategory(), categoryValue);
    }

    private static String markdownToHtml(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else {
            MutableDataSet options = new MutableDataSet();
            com.vladsch.flexmark.util.ast.Document document = Parser.builder(options).build().parse(value);
            return HtmlRenderer.builder(options).build().render(document);
        }
    }
}
