/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents {@link Process} model configuration.
 *
 * @author afelice
 */
public class ProcessConfiguration extends SchemaItemConfiguration {

    @JsonCreator
    public ProcessConfiguration() {
        super();
    }    
    
    /**
     *
     * @param name {@link Process} name.
     */
    public ProcessConfiguration(String name) {
        super(name);
    }

    @Override
    @JsonProperty("process")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        super.setCmdbSerialization(cmdbSerialization);
    }

    @Override
    @JsonProperty("process")
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

}
