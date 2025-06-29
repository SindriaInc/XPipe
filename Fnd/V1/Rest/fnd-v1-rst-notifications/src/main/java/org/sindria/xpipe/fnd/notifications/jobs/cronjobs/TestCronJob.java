package org.sindria.xpipe.fnd.notifications.jobs.cronjobs;

import org.sindria.xpipe.lib.nanoREST.job.CronJob;

public class TestCronJob extends CronJob {

    public TestCronJob() {
        super("* * * * *","Cron Job test-1", 2);
    }

    public void handle() {
        System.out.println("Sync data...");
    }
}