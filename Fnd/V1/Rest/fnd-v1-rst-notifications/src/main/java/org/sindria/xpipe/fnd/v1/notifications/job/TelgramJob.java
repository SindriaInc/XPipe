package org.sindria.xpipe.fnd.v1.notifications.job;

import org.sindria.xpipe.core.lib.nanorest.job.Job;

import org.sindria.xpipe.fnd.v1.notifications.Payload;

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
