package org.sindria.xpipe.lib.nanoREST.kernel;

import org.sindria.xpipe.lib.nanoREST.job.CronJob;

import java.io.IOException;
import java.util.Map;

public abstract class StatelessApp extends RestKernel {

    /**
     * StatelessApp void
     */
    public StatelessApp() throws IOException {
        super();
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
            System.out.println("No command provided. Use --command=<command_name> followed by arguments.");
            return;
        }

        Map<String, String> parsedArgs = CommandKernel.parseArguments(args);
        String commandType = parsedArgs.get("command");

        CommandKernel commandInstance = getCommands().get(commandType);
        if (commandInstance == null) {
            System.out.println("Unknown command: " + commandType);
            return;
        }

        commandInstance.run(args);
    }
}
