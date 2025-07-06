package org.sindria.xpipe.fnd.v1.notifications.job;

import org.sindria.xpipe.core.lib.nanorest.job.Job;

public class TestJob extends Job {

    public TestJob(String name, int priority) {
        super(name, priority);
    }

    public void handle() {
        System.out.println("Invio email...");
    }
}
