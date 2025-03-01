/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.LookupInfo;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Represent all model configurations
 * <ul>
 * <li>{@link ClasseConfiguration};
 * <li>{@link ProcessConfiguration};
 * <li>{@link DomainConfiguration};
 * <li>{@link SchemaLookupConfiguration};
 * </ul>
 * needed on mobile offline mode.* 
 * @author afelice
 */
public class SchemaConfiguration {

    @JsonProperty("_id")
    public final String id;
    public final String name;
    
    public List<ClasseConfiguration> classes = list();
    public List<ProcessConfiguration> processes = list();
    public List<DomainConfiguration> domains = list();

    /**
     * (lookupTypeName, {@link SchemaLookupConfiguration})
     */
    @JsonIgnore
    public Map<String, SchemaLookupConfiguration> internalLookups = map();

    // DMS
    public List<ClasseConfiguration> dmsModels = list();
    public Map<String, SchemaLookupConfiguration> dmsCategoryLookups = map();

    @JsonCreator
    public SchemaConfiguration(@JsonProperty("_id") String id, @JsonProperty("name") String name) {
        this.id = checkNotBlank(id);
        this.name = checkNotBlank(name);
    }

    @JsonProperty("lookups")
    public List<SchemaLookupConfiguration> getLookups() {
        return list(internalLookups.values());
    }

    @JsonProperty("lookups")
    public void setLookups(List<SchemaLookupConfiguration> lookups) {
        this.internalLookups = lookups.stream()
                .collect(Collectors.toMap(SchemaLookupConfiguration::getName, identity()));
    }

    public void addClasse(ClasseConfiguration classeConf) {
        classes.add(classeConf);
    }

    @JsonIgnore
    public List<String> getClasseNames() {
        return classes.stream().map(cc -> cc.name).collect(toList());
    }

    public void addProcess(ProcessConfiguration processConf) {
        processes.add(processConf);
    }

    public void addDomain(DomainConfiguration domainConf) {
        domains.add(domainConf);
    }

    public void addLookup(String lookupTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations,
            String lookupParentName) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for lookup =< %s >", lookupTypeName);
        
        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo(); 
        List<SchemaLookupValueConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            SchemaLookupValueConfiguration curValueConf = buildValueConfFrom(v.getId(), lookupTypeName, v);
            curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
            lookupValueConfs.add(curValueConf);
            
            if (!rawLookupInfo.hasType()) {
                rawLookupInfo.setType(v.getType());
            }
        });
        
        addLookupConfiguration(buildLookupConfiguration(lookupTypeName, rawLookupInfo, lookupValueConfs, lookupParentName));
    }
    
    public void addDmsModel(ClasseConfiguration dmsModelConf) {
        dmsModels.add(dmsModelConf);
    }    
    
    public void addDmsCategoryLookup(String dmsCategoryLookupTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for dms category lookup =< %s >", dmsCategoryLookupTypeName);
        
        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo(); 
        List<DmsCategoryConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            DmsCategoryConfiguration curValueConf = buildDmsCategoryConfFrom(v.getId(), dmsCategoryLookupTypeName, v);
            curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
            lookupValueConfs.add(curValueConf);
            
            if (!rawLookupInfo.hasType()) {
                rawLookupInfo.setType(v.getType());
            }
        });
        
        addDmsCategoryConfiguration(buildDmsLookupConfiguration(dmsCategoryLookupTypeName, rawLookupInfo, lookupValueConfs));        
    }    
    
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
    
    private void addLookupConfiguration(SchemaLookupConfiguration lookupConf) {
        internalLookups.put(lookupConf.getName(), lookupConf);
    }

    private void addDmsCategoryConfiguration(SchemaLookupConfiguration lookupConf) {
        dmsCategoryLookups.put(lookupConf.getName(), lookupConf);
    }
    
    private SchemaLookupConfiguration buildDmsLookupConfiguration(String dmsCategoryLookupTypeName, LookupInfo rawLookupInfo, 
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs) {
        return buildLookupConfiguration(dmsCategoryLookupTypeName, rawLookupInfo, lookupValueConfs, null);
    }
    
    private SchemaLookupConfiguration buildLookupConfiguration(String lookupName, LookupInfo rawLookupInfo, 
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs, String lookupParentName) {
        final LookupType rawLookupType = rawLookupInfo.getType();
        
        SchemaLookupConfiguration lookupConf = new SchemaLookupConfiguration(lookupName, lookupValueConfs);
        lookupConf.setAccessType(rawLookupType.getAccessType());
        lookupConf.setSpeciality(rawLookupType.getSpeciality());
        lookupConf.parent = lookupParentName;
        
        return lookupConf;
    }    

    private DmsCategoryConfiguration buildDmsCategoryConfFrom(long id, String lookupName, LookupValue lookupValue) {
        checkNotBlank(lookupValue.getCode(), "empty code for lookup value in dms category =< %s >".formatted(lookupName));
        checkNotBlank(lookupValue.getDescription(), "empty description for lookup value in dms category =< %s > for code =< %s >".formatted(lookupName, lookupValue.getCode()));
        DmsCategoryConfiguration result = new DmsCategoryConfiguration(id, lookupName, lookupValue.getCode(), lookupValue.getDescription());
        result.active = lookupValue.isActive();
        result.isDefault = lookupValue.isDefault();
        result.notes = lookupValue.getNotes();
        
        result.applyConfig(lookupValue.getConfig());
        
        return result;
    }
    
} // end ModelConfiguration class
