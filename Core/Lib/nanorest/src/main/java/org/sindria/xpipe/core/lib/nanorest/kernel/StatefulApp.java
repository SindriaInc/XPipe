package org.sindria.xpipe.core.lib.nanorest.kernel;

import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.sindria.xpipe.core.lib.nanorest.cronjob.CronJobDispatcher;
import org.sindria.xpipe.core.lib.nanorest.cronjob.CronJob;

import java.io.IOException;
import java.util.*;

public abstract class StatefulApp extends RestKernel {

    protected final CronJobDispatcher cronJobDispatcher;
    protected final List<String> commandHistory;

    public StatefulApp(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(typeController, apiVersion, serviceName);
        this.cronJobDispatcher = new CronJobDispatcher();
        this.commandHistory = new ArrayList<>();
    }

    public StatefulApp(Class typeController) throws IOException {
        super(typeController);
        this.cronJobDispatcher = new CronJobDispatcher();
        this.commandHistory = new ArrayList<>();
    }

    protected abstract Map<String, CommandKernel> getCommands();
    protected abstract Map<String, CronJob> getCronJobs();

    public final void handle(String[] args) {
        if (args.length == 0) {
            System.out.println("No command provided. Use --cron=true to run scheduler");
            System.out.println("Stateful app ready\n");
            this.console();
            return;
        }

        Map<String, String> parsedArgs = CommandKernel.parseArguments(args);
        boolean cronToggle = Boolean.parseBoolean(parsedArgs.get("cron"));

        if (cronToggle) {
            System.out.println("Starting Cronjob Scheduler\n");
            for (var cronJob : getCronJobs().values()) {
                this.cronJobDispatcher.scheduleCronJob(cronJob);
            }
        }

        System.out.println("Stateful app ready\n");
        this.console();
    }

    public void console() {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(new DefaultParser())
                    .completer(new CommandCompleter(getCommands().keySet()))
                    .build();

            System.out.println("Starting console command\n");

            while (true) {
                String input;
                try {
                    input = reader.readLine("> ").trim();
                } catch (UserInterruptException e) {
                    System.out.println("Exiting...");
                    break;
                } catch (EndOfFileException e) {
                    break;
                }

                if (input.isEmpty()) continue;

                commandHistory.add(input);

                if (input.equalsIgnoreCase("/exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                processCommand(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing terminal. Falling back to simple input mode.");
            fallbackConsole();
        }
    }

    private void fallbackConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("/exit")) {
                System.out.println("Exiting...");
                break;
            }

            commandHistory.add(input);
            processCommand(input);
        }
    }

    private void processCommand(String input) {
        String[] parsedArgs = input.split("\\s+");
        String commandWithSlash = parsedArgs[0].trim();
        String command = commandWithSlash.substring(1);

        if (command.equalsIgnoreCase("help")) {
            this.help();
            return;
        }

        CommandKernel commandInstance = getCommands().get(command);
        if (commandInstance == null) {
            System.out.println("Unknown command: " + commandWithSlash);
        } else {
            commandInstance.run(parsedArgs);
        }
    }

    public void help() {
        System.out.println("Usage: <command> <args>");
        System.out.println("\nAvailable commands:");
        for (String cmd : getCommands().keySet()) {
            System.out.println("/" + cmd);
        }
        System.out.println();
    }
}
