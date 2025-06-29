package org.sindria.xpipe.fnd.notifications;

import org.sindria.xpipe.lib.nanoREST.kernel.StatefulApp;
import org.sindria.xpipe.lib.nanoREST.job.CronJob;
import org.sindria.xpipe.fnd.notifications.jobs.cronjobs.TestCronJob;

import org.sindria.xpipe.lib.nanoREST.kernel.StatelessApp;
import org.sindria.xpipe.lib.nanoREST.kernel.CommandKernel;
import org.sindria.xpipe.lib.nanoREST.commands.ClearCommand;
import org.sindria.xpipe.lib.nanoREST.commands.PrintCommand;
import org.sindria.xpipe.fnd.notifications.commands.SumCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App extends StatefulApp {

    /**
     * App constructor
     */
    protected App() throws IOException {

        // Stateful App V1
        //super(Controller.class, "v1", "blog");

        // Stateful App V2
        super(Controller.class);

        // Stateless App
        //super();
    }

    public static void main(String[] args) throws IOException {
        new App().handle(args);
    }

    /**
     * Register routes
     */
    @Override
    public HashMap<String, String> appRoutes() {
        HashMap<String, String> routes = new HashMap<>();

        routes.put("test", "Controller::test");
        routes.put("handle", "Controller::handle");

        return routes;
    }

    /**
     * Register commands
     */
    @Override
    protected Map<String, CommandKernel> getCommands() {
        Map<String, CommandKernel> commands = new HashMap<>();
        commands.put("clear", new ClearCommand());
        commands.put("print", new PrintCommand());
        commands.put("sum", new SumCommand());
        return commands;
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

    /**
     * Register cronjobs
     */
    @Override
    protected Map<String, CronJob> getCronJobs() {
        Map<String, CronJob> cronjobs = new HashMap<>();
        cronjobs.put("test-1", new TestCronJob());
        cronjobs.put("test-2", new CronJob("* * * * *", "Cron Job test-2", 1));
        return cronjobs;
    }

}