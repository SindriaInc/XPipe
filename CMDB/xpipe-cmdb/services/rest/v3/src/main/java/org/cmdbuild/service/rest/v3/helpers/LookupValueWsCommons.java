package org.cmdbuild.service.rest.v3.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.lookup.LookupValueImpl.LookupBuilder;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.lookup.LookupValue;

public abstract class LookupValueWsCommons {

    protected final LookupService lookupService;
    protected final ObjectTranslationService translationService;

    protected LookupValueWsCommons(LookupService lookupLogic, ObjectTranslationService translationService) {
        this.lookupService = checkNotNull(lookupLogic);
        this.translationService = checkNotNull(translationService);
    }

    protected Object doRead(String lookupTypeId, Long lookupValueId) {
        LookupValue lookup = lookupService.getLookup(lookupValueId);
        return response(toResponse(lookup));
    }

    protected Object doReadAll(String lookupTypeId, Integer limit, Integer offset, String filterStr, String viewMode) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        PagedElements<LookupValue> lookups = isAdminViewMode(viewMode) ? lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter) : lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        return response(lookups.stream().map(this::toResponse).collect(toList()), lookups.totalSize());
    }

    protected Object doReadDistinct(String lookupTypeId, Integer limit, Integer offset, String viewMode, String forClass, String forAttr) {
        PagedElements<LookupValue> lookups = lookupService.getDistinctActiveLookup(decodeIfHex(lookupTypeId), offset, limit, forClass, forAttr);
        return response(lookups.stream().map(this::toResponse).collect(toList()), lookups.totalSize());
    }

    protected Object doCreate(String lookupTypeId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).build());
        return response(toResponse(lookup));
    }

    protected Object doUpdate(String lookupTypeId, Long lookupId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).withId(checkNotNull(lookupId)).build());
        return response(toResponse(lookup));
    }

    protected Object doDelete(String lookupTypeId, Long lookupId) {
        lookupService.deleteLookupValue(decodeIfHex(lookupTypeId), lookupId);
        return success();
    }

    protected Object doReorder(String lookupTypeId, List<Long> lookupValueIds, String viewMode) {
        lookupTypeId = decodeIfHex(lookupTypeId);
        checkNotNull(lookupValueIds);
        checkArgument(set(lookupValueIds).size() == lookupValueIds.size());
        checkArgument(lookupValueIds.stream().allMatch(notNull()));

        List<LookupValue> lookups = list(lookupService.getAllLookup(lookupTypeId));

        List<LookupValue> lookupsToSave = list();

        for (int i = 0; i < lookupValueIds.size(); i++) {
            Long lookupId = lookupValueIds.get(i);
            LookupValue lookup = lookups.stream().filter((l) -> equal(l.getId(), lookupId)).collect(onlyElement());
            int newIndex = i + 1;
            if (newIndex != lookup.getIndex()) {
                lookupsToSave.add(LookupValueImpl.copyOf(lookup).withIndex(newIndex).build());
            }
        }

        lookupsToSave.forEach(lookupService::createOrUpdateLookup);

        return doReadAll(lookupTypeId, null, null, null, viewMode);
    }

    protected Object toResponse(LookupValue lookup) {
        return map(
                "_id", lookup.getId(),
                "_type", lookup.getType().getName(),
                "code", lookup.getCode(),
                "description", lookup.getDescription(),
                "_description_translation", translationService.translateLookupDescriptionSafe(lookup.getType().getName(), lookup.getCode(), lookup.getDescription()),
                "index", lookup.getIndex(),
                "active", lookup.isActive(),
                "parent_id", lookup.getParentId(),
                "parent_type", Optional.ofNullable(lookup.getParentTypeOrNull()).map(p -> lookupService.getLookupType(p).getName()).orElse(null),
                "default", lookup.isDefault(),
                "note", lookup.getNotes(),
                "text_color", lookup.getTextColor(),
                "icon_type", lookup.getIconType().name().toLowerCase(),
                "icon_image", lookup.getIconImage(),
                "icon_font", lookup.getIconFont(),
                "icon_color", lookup.getIconColor(),
                "accessType", serializeEnum(lookupService.getLookupType(lookup.getLookupType()).getAccessType())).accept(m -> {
            if (lookup.getType().isDmsCategorySpeciality()) {
                m.put(
                        "modelClass", lookup.getConfig().getDmsModelClass(),
                        "allowedExtensions", Joiner.on(",").join(nullToEmpty(lookup.getConfig().getDmsAllowedExtensions())),
                        "checkCount", serializeEnum(lookup.getConfig().getDmsCheckCount()),
                        "checkCountNumber", lookup.getConfig().getDmsCheckCountNumber(),
                        "maxFileSize", lookup.getConfig().getMaxFileSize());
            }
        });
    }

    public static class WsLookupValue {

        private final Long parentId;
        private final Integer index;
        private final Boolean isDefault, active;
        private final String code, description, iconType, iconImage, iconFont, iconColor, textColor, notes, modelClass;
        private final Collection<String> allowedExtensions;
        private final DmsAttachmentCountCheck checkCount;
        private final Integer checkCountNumber, maxFileSize;

        public WsLookupValue(
                @JsonProperty("parent_id") Long parentId,
                @JsonProperty("index") Integer index,
                @JsonProperty("default") Boolean isDefault,
                @JsonProperty("active") Boolean active,
                @JsonProperty("code") String code,
                @JsonProperty("description") String description,
                @JsonProperty("icon_type") String iconType,
                @JsonProperty("icon_image") String iconImage,
                @JsonProperty("icon_font") String iconFont,
                @JsonProperty("icon_color") String iconColor,
                @JsonProperty("text_color") String textColor,
                @JsonProperty("modelClass") String modelClass,
                @JsonProperty("allowedExtensions") String allowedExtensions,
                @JsonProperty("checkCount") String checkCount,
                @JsonProperty("checkCountNumber") Integer checkCountNumber,
                @JsonProperty("maxFileSize") Integer maxFileSize,
                @JsonProperty("note") String notes) {
            this.parentId = parentId;
            this.index = index;
            this.isDefault = isDefault;
            this.code = checkNotBlank(code);
            this.description = description;
            this.iconType = iconType;
            this.iconImage = iconImage;
            this.iconFont = iconFont;
            this.iconColor = iconColor;
            this.textColor = textColor;
            this.notes = notes;
            this.active = active;
            this.modelClass = modelClass;
            this.allowedExtensions = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(Strings.nullToEmpty(allowedExtensions));
            this.maxFileSize = maxFileSize;
            this.checkCount = parseEnumOrNull(checkCount, DmsAttachmentCountCheck.class);
            this.checkCountNumber = checkCountNumber;
        }

        private LookupBuilder buildLookup() {
            return LookupValueImpl.builder()
                    .withCode(code)
                    .withConfig(b -> b
                    .withIconColor(iconColor)
                    .withTextColor(textColor)
                    .withDefault(isDefault)
                    .withIconFont(iconFont)
                    .withIconImage(iconImage)
                    .withIconType(iconType)
                    .withAllowedExtensions(allowedExtensions)
                    .withCountCheck(checkCount)
                    .withCountCheckNumber(checkCountNumber)
                    .withMaxFileSize(maxFileSize)
                    .withDmsModel(modelClass))
                    .withDescription(description)
                    .withNotes(notes)
                    .withIndex(index)
                    .withActive(active)
                    .withParentId(parentId);
        }

    }
}
