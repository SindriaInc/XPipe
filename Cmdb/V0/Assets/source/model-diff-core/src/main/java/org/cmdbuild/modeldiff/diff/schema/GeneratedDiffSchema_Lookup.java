/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents a <i>diff</i>, a changed {@link LookupType}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_Lookup extends GeneratedDiffSchema_SchemaItem {

    @JsonProperty("lookup")
    @Override
    public Map<String, Object> getItemProperties() {
        return itemProperties;
    }

    @JsonProperty("lookup")
    @Override
    public void setItemProperties(Map<String, Object> itemProperties) {
        this.itemProperties = itemProperties;
    }

    @JsonProperty("values")
    @Override
    public List<Map<String, Object>> getItemAttributes() {
        return super.getItemAttributes();
    }

    @JsonProperty("values")
    @Override
    public void setItemAttributes(List<Map<String, Object>> itemAttributes) {
        super.setItemAttributes(itemAttributes);
    }

}
