/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.lookup.LookupAccessType;
import org.cmdbuild.lookup.LookupSpeciality;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_LOOKUP_VALUES_SERIALIZATION;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 * As serialized in {@link LookupTypeWs#toResponse()}.
 *
 * <p>
 * <b>Note</b>: in <i>schema diff</i> even <code>speciality/i> and
 * <i>accessType</i> are deserialized.
 *
 * @author afelice
 */
public class SchemaLookupConfiguration {

    private final String name;
    @JsonProperty("_id")
    private final String id;
    public String parent;
    
    private LookupSpeciality speciality;
    private LookupAccessType accessType;
    
    private final List<SchemaLookupValueConfiguration> values; 
        
    public SchemaLookupConfiguration(String name) {
        this(name, list());
    }

    @JsonCreator
    public SchemaLookupConfiguration(@JsonProperty("name") String name, @JsonProperty(ATTR_LOOKUP_VALUES_SERIALIZATION) List<? extends SchemaLookupValueConfiguration> values) {
        this.name = checkNotNull(name);
        this.id = this.name; // See LookupTypeWs.toResponse
        checkArgument(!CmCollectionUtils.isNullOrEmpty(values), "empty map found for lookup =< %s >", name);
        this.values = list();
        values.forEach(lvConf -> this.values.add(lvConf));
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }    

    public List<SchemaLookupValueConfiguration> getValues() {
        return values;
    }

    public void addValue(SchemaLookupValueConfiguration value) {
        values.add(value);
    }
    
    @JsonIgnore
    public LookupSpeciality getSpeciality() {
        return speciality;
    }

    @JsonProperty("speciality")
    public String getSpecialityStr() {
        return serializeEnum(speciality);
    }

    public void setSpeciality(LookupSpeciality speciality) {
        this.speciality = speciality;
    }

    @JsonProperty("speciality")
    public void setSpeciality(String specialityStr) {
        this.speciality = parseEnumOrDefault(specialityStr, LookupSpeciality.LS_DEFAULT);
    }
    
    @JsonIgnore
    public LookupAccessType getAccessType() {
        return accessType;
    }

    @JsonProperty("accessType")
    public String getAccessTypeStr() {
        return serializeEnum(accessType);
    }

    public void setAccessType(LookupAccessType accessType) {
        this.accessType = accessType;
    }

    @JsonProperty("accessType")
    public void setAccessType(String accessTypeStr) {
        this.accessType = parseEnumOrDefault(accessTypeStr, LookupAccessType.LT_DEFAULT);
    }

}
