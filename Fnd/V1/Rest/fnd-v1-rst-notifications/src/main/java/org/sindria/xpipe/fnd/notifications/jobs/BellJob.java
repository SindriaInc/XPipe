package org.sindria.xpipe.fnd.notifications.jobs;

import org.json.JSONObject;
import org.sindria.xpipe.fnd.notifications.Helper;
import org.sindria.xpipe.fnd.notifications.Payload;
import org.sindria.xpipe.lib.nanoREST.job.Job;

public class BellJob extends Job {

    private final Payload payload;

    public BellJob(String name, int priority, Payload payload) {
        super(name, priority);
        this.payload = payload;
    }

    public void handle() {


        System.out.println("DUMP PAYLOAD DATA");
        System.out.println(payload.getData().serialize());

        JSONObject response = (JSONObject) Helper.post("/rest/V1/core/notifications/receive", payload.getData().serialize());
        System.out.println(response);
        System.out.println("Sending notification to bell channel " + payload.serialize());
    }
}
