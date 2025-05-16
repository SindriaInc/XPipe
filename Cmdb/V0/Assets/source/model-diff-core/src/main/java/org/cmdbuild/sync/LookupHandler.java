/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.LookupInfo;
import org.cmdbuild.modeldiff.data.ModelConfiguration;
import org.cmdbuild.modeldiff.schema.SchemaLookupConfiguration;
import org.cmdbuild.modeldiff.schema.SchemaLookupValueConfiguration;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

// @todo AFE TBC unificare con org.cmdbuild.model.dataset.data.LookupHandler? Attenzione agli attributi deserializzati in più qui
/**
 * Handles data for a real <i>schema</i> * {@link LookupType} stuff, composed of
 * value items.
 *
 * <p>
 * Contains even<i>lookup parent types</i>.
 *
 * @author afelice
 */
public class LookupHandler {

    // Cached map
    private final Map<Long, LookupType> parentTypes = map();
    
    private final Map<LookupType, List<LookupValue>> alreadyTracedLookup = map();

    /**
     * Resulting (lookupTypeName, {@link SchemaLookupConfiguration})
     */
    public Map<String, SchemaLookupConfiguration> lookups = map();

    private final LookupService lookupService;
    private final ObjectTranslationService translationService;

    public LookupHandler(LookupService lookupService, ObjectTranslationService translationService) {
        this.lookupService = lookupService;
        this.translationService = translationService;
    }

    /**
     * Similar to {@link ModelConfiguration#addLookup()}
     *
     * @param lookupType
     * @param valuesList
     */
    public void addLookup(LookupType lookupType, List<LookupValue> valuesList) {
        checkArgument(!isNullOrEmpty(valuesList), "empty values found for lookup =< %s >", lookupType.getName());

        if (!isAlreadyTreated(lookupType.getName())) {
            alreadyTracedLookup.put(lookupType, valuesList);

            String lookupParentName = null;

            if (lookupType.hasParent() && !valuesList.isEmpty()) {
                // fetch parent lookup from id
                lookupParentName = fetchLookupParent(valuesList);
            }
            final String finalLookupParentName = lookupParentName; // to be used in forEach() lambda

            // (code, description_translation )
            Map<String, String> valueDescrTranslations = fetchLookupDescrTranslations(lookupType.getName(), valuesList);


            // Wrapper object for lookup type and config, not using directly the contained attributes,
            // to avoid "local variable referenced from a lambda expression must be declared as finel" error
            LookupInfo rawLookupInfo = new LookupInfo();
            List<SchemaLookupValueConfiguration> lookupValueConfs = list();
            valuesList.stream().forEach(v -> {
                SchemaLookupValueConfiguration curValueConf = buildValueConfFrom(v.getId(), lookupType.getName(), v);
                curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
                lookupValueConfs.add(curValueConf);

                if (finalLookupParentName != null) {
                    curValueConf.lookupParentName = finalLookupParentName;
                }

                if (!rawLookupInfo.hasType()) {
                    rawLookupInfo.setType(v.getType());
                }
            });

            addLookupConfiguration(buildLookupConfiguration(lookupType.getName(), rawLookupInfo, lookupValueConfs, lookupParentName));

        }
    }

    public Map<LookupType, List<LookupValue>> getLookups() {
        return alreadyTracedLookup;
    }

    public boolean isAlreadyTreated(String lookupTypeName) {
        return alreadyTracedLookup.keySet().stream().filter(lt -> lt.getName().equals(lookupTypeName)).findFirst().isPresent();
    }

    /**
     *
     * @param value
     * @param parentTypes
     * @return
     */
    public String fetchLookupParent(List<LookupValue> value) {
        Long parentId = value.iterator().next().getType().getParent();

        if (parentId == null) {
            return null;
        }

        parentTypes.computeIfAbsent(parentId, id -> lookupService.getLookupType(id));
        LookupType parentType = parentTypes.get(parentId);

        if (alreadyTracedLookup.keySet().stream()
                .filter(lt -> lt.getName().equals(parentType.getName())).findAny()
                .isEmpty()) {
            List<LookupValue> parentValues = lookupService.getAllLookup(parentType).elements();
            addLookup(parentType, parentValues);
        }

        return parentType.getName();
    }

    private void addLookupConfiguration(SchemaLookupConfiguration lookupConf) {
        lookups.put(lookupConf.getName(), lookupConf);
    }

    /**
     * As in {@link DmsSynchImpl#fetchDmsCategoryDescrTranslations()}.
     *
     * @param lookupTypeName
     * @param valuesList
     * @return
     */
    public Map<String, String> fetchLookupDescrTranslations(String lookupTypeName, List<LookupValue> valuesList) {
        return valuesList.stream()
                .collect(
                        Collectors.toMap(
                                LookupValue::getCode,
                                v -> translationService.translateLookupDescriptionSafe(lookupTypeName, v.getCode(), v.getDescription())
                        )
                );
    }

    /**
     * As in {@link ModelConfiguration}.
     *
     * @param lookupName
     * @param rawLookupInfo
     * @param lookupValueConfs
     * @param lookupParentName
     * @return
     */
    private SchemaLookupConfiguration buildLookupConfiguration(String lookupName, LookupInfo rawLookupInfo,
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs, String lookupParentName) {
        final LookupType rawLookupType = rawLookupInfo.getType();

        SchemaLookupConfiguration lookupConf = new SchemaLookupConfiguration(lookupName, lookupValueConfs);
        lookupConf.setAccessType(rawLookupType.getAccessType());
        lookupConf.setSpeciality(rawLookupType.getSpeciality());
        lookupConf.parent = lookupParentName;

        return lookupConf;
    }

    /**
     * As in {@link ModelConfiguration}.
     *
     * @param id
     * @param lookupName
     * @param lookupValue
     * @return
     */
    private SchemaLookupValueConfiguration buildValueConfFrom(long id, String lookupName, LookupValue lookupValue) {
        checkNotBlank(lookupValue.getCode(), "empty code for lookup value in lookup =< %s >".formatted(lookupName));
        checkNotBlank(lookupValue.getDescription(), "empty description for lookup value in lookup =< %s > for code =< %s >".formatted(lookupName, lookupValue.getCode()));
        SchemaLookupValueConfiguration result = new SchemaLookupValueConfiguration(id, lookupName, lookupValue.getCode(), lookupValue.getDescription());
        result.active = lookupValue.isActive();
        result.isDefault = lookupValue.isDefault();
        result.notes = lookupValue.getNotes();

        result.applyConfig(lookupValue.getConfig());

        return result;
    }
    
} // end LookupHandler class
