/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.lookup.IconType;
import org.cmdbuild.modeldiff.core.LookupValueConfiguration;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;

/**
 * See {@link LookupValueWsCommons#toResponse(LookupValue)}
 *
 * <p>
 * <b>Note</b>: in <i>schema diff</i> even <code>iconType</code> is
 * deserialized.
 *
 * @author afelice
 */
public class SchemaLookupValueConfiguration extends LookupValueConfiguration {
    
    @JsonCreator
    public SchemaLookupValueConfiguration(@JsonProperty("_id") Long id, @JsonProperty("_type") String lookupName,
            @JsonProperty("code") String code, @JsonProperty("description") String description) {
        super(id, lookupName, code, description);
    }
   
    @JsonProperty("iconType")
    public void setIconTypeStr(String iconTypeStr) {
        this.iconType = parseEnumOrDefault(iconTypeStr, IconType.NONE);
    }
    
}
