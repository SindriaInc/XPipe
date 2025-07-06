package org.sindria.xpipe.core.lib.nanorest.bitbucket.model;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

public class UpdateConfiguration {

    private final boolean enabled;

    public UpdateConfiguration(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
