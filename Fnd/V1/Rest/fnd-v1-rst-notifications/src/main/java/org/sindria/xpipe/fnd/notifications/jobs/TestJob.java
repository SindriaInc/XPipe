package org.sindria.xpipe.fnd.notifications.jobs;

import org.sindria.xpipe.lib.nanoREST.job.Job;

public class TestJob extends Job {

    public TestJob(String name, int priority) {
        super(name, priority);
    }

    public void handle() {
        System.out.println("Invio email...");
    }
}
