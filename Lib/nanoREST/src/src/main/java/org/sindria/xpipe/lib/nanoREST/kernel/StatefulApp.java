package org.sindria.xpipe.lib.nanoREST.kernel;

import org.sindria.xpipe.lib.nanoREST.job.CronJobDispatcher;
import org.sindria.xpipe.lib.nanoREST.job.CronJob;

import java.io.IOException;
import java.util.Map;

public abstract class StatefulApp extends RestKernel {

    /**
     * cronJobDispatcher
     */
    protected final CronJobDispatcher cronJobDispatcher;

    /**
     * BaseApp constructor v1 hardcoded
     */
    public StatefulApp(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(typeController, apiVersion, serviceName);
        this.cronJobDispatcher = new CronJobDispatcher();
    }

    /**
     * BaseApp constructor v2 config
     */
    public StatefulApp(Class typeController) throws IOException {
        super(typeController);
        this.cronJobDispatcher = new CronJobDispatcher();
    }

    /**
     * Provides a mapping of command names to their respective command implementations.
     * @return A map where the key is the command name and the value is the command instance.
     */
    protected abstract Map<String, CommandKernel> getCommands();

    /**
     * Provides a mapping of command names to their respective cronjob implementations.
     * @return A map where the key is the cronjob name and the value is the CronJob instance.
     */
    protected abstract Map<String, CronJob> getCronJobs();

    /**
     * Handles the execution of commands dynamically.
     * @param args The command-line arguments.
     */
    public final void handle(String[] args) {

        if (args.length == 0) {
            System.out.println("No command provided. Use --cron=true to run scheduler");
            System.out.println("Stateful app ready");
            return;
        }

        Map<String, String> parsedArgs = CommandKernel.parseArguments(args);
        boolean cronToggle = Boolean.parseBoolean(parsedArgs.get("cron"));

        if (cronToggle) {
            System.out.println("Starting Cronjob Scheduler");
            for (var cronJob : getCronJobs().values()) {
                this.cronJobDispatcher.scheduleCronJob(cronJob);
            }
        }

        System.out.println("Stateful app ready");

        // TODO: implement console command like minecraft server

    }
}
