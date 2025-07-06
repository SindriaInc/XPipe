package org.sindria.xpipe.core.lib.nanorest.github.model;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

import java.util.HashMap;

public class Workflow {

    private final String ref;

    private final HashMap<String, String> inputs;

    public Workflow(String ref, HashMap<String, String> inputs) {
        this.ref = ref;
        this.inputs = inputs;
    }

    public String getRef() {
        return ref;
    }

    public HashMap<String, String> getInputs() {
        return inputs;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
