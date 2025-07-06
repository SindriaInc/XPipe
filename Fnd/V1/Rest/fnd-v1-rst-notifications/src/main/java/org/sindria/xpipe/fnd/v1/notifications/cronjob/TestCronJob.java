package org.sindria.xpipe.fnd.v1.notifications.cronjob;

import org.sindria.xpipe.core.lib.nanorest.job.CronJob;

public class TestCronJob extends CronJob {

    public TestCronJob() {
        super("* * * * *","Cron Job test-1", 2);
    }

    public void handle() {
        System.out.println("Sync data...");
    }
}