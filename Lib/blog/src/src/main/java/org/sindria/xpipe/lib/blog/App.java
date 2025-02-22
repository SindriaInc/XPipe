package org.sindria.xpipe.lib.blog;

import org.sindria.xpipe.lib.nanoREST.kernel.StatefulApp;
import org.sindria.xpipe.lib.nanoREST.kernel.StatelessApp;
import org.sindria.xpipe.lib.nanoREST.kernel.CommandKernel;
import org.sindria.xpipe.lib.nanoREST.commands.PrintCommand;
import org.sindria.xpipe.lib.blog.commands.SumCommand;

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
        routes.put("sample", "Controller::sample");
        routes.put("abba", "Controller::abba");

        return routes;
    }

    /**
     * Register commands
     */
    @Override
    protected Map<String, CommandKernel> getCommands() {
        Map<String, CommandKernel> commands = new HashMap<>();
        commands.put("print", new PrintCommand());
        commands.put("sum", new SumCommand());
        return commands;
    }

}