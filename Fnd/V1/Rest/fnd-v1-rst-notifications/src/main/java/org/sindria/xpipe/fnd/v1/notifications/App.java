package org.sindria.xpipe.fnd.v1.notifications;

import org.sindria.xpipe.core.lib.nanorest.kernel.StatefulApp;
import org.sindria.xpipe.core.lib.nanorest.cronjob.CronJob;
import org.sindria.xpipe.core.lib.nanorest.kernel.CommandKernel;
import org.sindria.xpipe.core.lib.nanorest.registry.ActionRegistry;
import org.sindria.xpipe.core.lib.nanorest.command.ClearCommand;
import org.sindria.xpipe.core.lib.nanorest.command.PrintCommand;

import org.sindria.xpipe.fnd.v1.notifications.action.HelloWorldAction;
import org.sindria.xpipe.fnd.v1.notifications.cronjob.TestCronJob;
import org.sindria.xpipe.fnd.v1.notifications.command.SumCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App extends StatefulApp {

    /**
     * App constructor
     */
    public App() throws IOException {
        super("v1", "notifications");
        this.registerActions();
    }

    public static void main(String[] args) throws IOException {
        new App().handle(args);
    }

    /**
     * Register Actions
     */
    protected void registerActions() {
        String base = "/api/" + apiVersion + "/" + serviceName;

        // Example action registration
        ActionRegistry.register(base + "/hello", new HelloWorldAction());

        // Register other actions here
    }

    @Override
    protected Map<String, CommandKernel> getCommands() {
        Map<String, CommandKernel> commands = new HashMap<>();
        commands.put("clear", new ClearCommand());
        commands.put("print", new PrintCommand());
        commands.put("sum", new SumCommand());
        return commands;
    }

    @Override
    protected Map<String, CronJob> getCronJobs() {
        Map<String, CronJob> cronjobs = new HashMap<>();
        cronjobs.put("test-1", new TestCronJob());
        cronjobs.put("test-2", new CronJob("* * * * *", "Cron Job test-2", 1));
        return cronjobs;
    }

    @Override
    public void help() {
        System.out.println("Usage: <command> <args>");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("/help");
        System.out.println("/print --message=<message>");
        System.out.println("/sum --a=<int> --b=<int>");
        System.out.println();
    }
}
