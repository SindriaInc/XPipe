package org.sindria.xpipe.fnd.v1.notifications;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

import java.util.HashMap;

public class PayloadData {

    private final String severity;
    private final String entity;
    private final String event;
    private final String details;
    private final String url;
    private final boolean isInternal;

    public PayloadData(String severity, String entity, String event, String details, String url, boolean isInternal) {
        this.severity = severity;
        this.entity = entity;
        this.event = event;
        this.details = details;
        this.url = url;
        this.isInternal = isInternal;
    }


    public String getSeverity() {
        return severity;
    }

    public String getEntity() {
        return entity;
    }

    public String getEvent() {
        return event;
    }

    public String getDetails() {
        return details;
    }

    public String getUrl() {
        return url;
    }

    public boolean isInternal() {
        return isInternal;
    }


    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
