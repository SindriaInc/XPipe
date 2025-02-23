package org.sindria.xpipe.lib.nanoREST.libs.bitbucket.models;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

import java.util.HashMap;

public class CreateRepository {

    public final String scm;

    public final boolean isPrivate;

    public final HashMap<String, String> project;

    public CreateRepository(String scm, boolean isPrivate, HashMap<String, String> project) {
        this.scm = scm;
        this.isPrivate = isPrivate;
        this.project = project;
    }

    public String getScm() {
        return scm;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public HashMap<String, String> getProject() {
        return project;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }

}
