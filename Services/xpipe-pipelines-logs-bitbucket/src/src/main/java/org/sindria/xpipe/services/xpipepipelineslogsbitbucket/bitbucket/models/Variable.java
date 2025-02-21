package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

public class Variable {

    private final String key;

    private final String value;

    private final boolean secured;

    public Variable(String key, String value, boolean secured) {
        this.key = key;
        this.value = value;
        this.secured = secured;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isSecured() {
        return secured;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
