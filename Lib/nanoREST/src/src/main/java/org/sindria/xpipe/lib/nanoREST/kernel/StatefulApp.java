package org.sindria.xpipe.lib.nanoREST.kernel;

import org.sindria.xpipe.lib.nanoREST.commands.PrintCommand;
import org.sindria.xpipe.lib.nanoREST.commands.SumCommand;
import org.sindria.xpipe.lib.nanoREST.job.CronJobDispatcher;
import org.sindria.xpipe.lib.nanoREST.job.CronJob;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public abstract class StatefulApp extends RestKernel {

    /**
     * cronJobDispatcher
     */
    protected final CronJobDispatcher cronJobDispatcher;

    /**
     * scanner
     */
    protected final Scanner scanner;

    /**
     * BaseApp constructor v1 hardcoded
     */
    public StatefulApp(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(typeController, apiVersion, serviceName);
        this.cronJobDispatcher = new CronJobDispatcher();
        this.scanner = new Scanner(System.in);
    }

    /**
     * BaseApp constructor v2 config
     */
    public StatefulApp(Class typeController) throws IOException {
        super(typeController);
        this.cronJobDispatcher = new CronJobDispatcher();
        this.scanner = new Scanner(System.in);
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
            this.console();
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
        this.console();
    }


    public void console() {

        System.out.println("Starting console command");
        System.out.println();

        while (true) {
            System.out.println("Type /help for command list: ");
            String input = scanner.nextLine();
            String[] parsedArgs = input.split("\\s+");

            String command = parsedArgs[0].trim();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            CommandKernel commandInstance;
            if (command.equalsIgnoreCase("/print")) {
                commandInstance = new PrintCommand();
            } else if (command.equalsIgnoreCase("/sum")) {
                commandInstance = new SumCommand();
            } else {
                System.out.println("Unknown command.");
                continue;
            }

            commandInstance.run(parsedArgs);

        }


    }

}
