package org.sindria.xpipe.fnd.v1.notifications.job;

import org.sindria.xpipe.core.lib.nanorest.job.Job;

import org.json.JSONObject;

import org.sindria.xpipe.fnd.v1.notifications.Helper;
import org.sindria.xpipe.fnd.v1.notifications.Payload;

public class BellJob extends Job {

    private final Payload payload;

    public BellJob(String name, int priority, Payload payload) {
        super(name, priority);
        this.payload = payload;
    }

    public void handle() {

        JSONObject response = (JSONObject) Helper.post("/rest/V1/core/notifications/receive", payload.getData().serialize());
        System.out.println(response);

        System.out.println("Sending notification to bell channel " + payload.serialize());
    }
}
