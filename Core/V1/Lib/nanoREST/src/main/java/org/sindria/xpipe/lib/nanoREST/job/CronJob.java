package org.sindria.xpipe.lib.nanoREST.job;

public class CronJob extends Job {

    public final String cronExpression;

    public CronJob(String cronExpression, String name, int priority) {
        super(name, priority);
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public void execute() {
        System.out.println("Executing cronjob: " + name + " with priority " + priority);
        this.handle();
        System.out.println("CronJob " + name + " completed.");
    }
}
