/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.etl.config.WaterwayItemConfig;
import org.cmdbuild.etl.config.WaterwayDescriptor;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WaterwayDescriptorImpl implements WaterwayDescriptor {

    private final String code, description, notes, tag;
    private final List<WaterwayItemConfig> items;
    @JsonAnySetter
    private final Map<String, String> config = CmMapUtils.map();

    public WaterwayDescriptorImpl(@JsonProperty(WY_DESCRIPTOR_CODE) String code, @JsonProperty(WY_DESCRIPTOR_DESCRIPTION) String description, @JsonProperty("notes") String notes, @JsonProperty("tag") String tag, @JsonProperty("items") ArrayNode items) {
        this.code = checkNotBlank(code);
        this.description = Strings.nullToEmpty(description);
        this.notes = Strings.nullToEmpty(notes);
        this.tag = nullToEmpty(tag);
        this.items = ImmutableList.copyOf(CmJsonUtils.fromJson(items, new TypeReference<List<WaterwayItemConfigImpl>>() {
        }));
        Preconditions.checkArgument(CmCollectionUtils.list(this.items).map(WaterwayItemConfig::getCode).duplicates().isEmpty(), "invalid config: duplicate item codes");
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public List<WaterwayItemConfig> getItems() {
        return items;
    }

    @Override
    public Map<String, String> getConfig() {
        return Collections.unmodifiableMap(config);
    }

    @Override
    public String toString() {
        return "WaterwayDescriptor{" + "code=" + code + '}';
    }

}
