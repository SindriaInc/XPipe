package org.sindria.xpipe.core.lib.nanorest.github.model;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

public class Variable {

    private final String name;

    private final String value;


    public Variable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }

}
