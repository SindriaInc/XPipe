package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

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
