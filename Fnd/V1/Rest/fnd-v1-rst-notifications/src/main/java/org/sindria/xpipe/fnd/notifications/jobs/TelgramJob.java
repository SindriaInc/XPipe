package org.sindria.xpipe.fnd.notifications.jobs;

import org.sindria.xpipe.fnd.notifications.Payload;
import org.sindria.xpipe.lib.nanoREST.job.Job;

public class TelgramJob extends Job {

    private final Payload payload;

    public TelgramJob(String name, int priority, Payload payload) {
        super(name, priority);
        this.payload = payload;
    }

    public void handle() {
        System.out.println("Sending notification to telegram channel " + payload.serialize());
    }
}
