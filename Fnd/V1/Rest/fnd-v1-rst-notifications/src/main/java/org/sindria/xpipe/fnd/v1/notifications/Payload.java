package org.sindria.xpipe.fnd.v1.notifications;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.serializer.JsonSerializer;

public class Payload {

    private final String channel;


    private final PayloadData data;

    public Payload(String channel, PayloadData data) {
        this.channel = channel;

        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public PayloadData getData() {
        return data;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
