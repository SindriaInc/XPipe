package org.sindria.xpipe.lib.blog;

import org.sindria.xpipe.lib.nanoREST.BaseApp;

import java.io.IOException;
import java.util.HashMap;

public class App extends BaseApp<Controller> {

    /**
     * App constructor
     */
    protected App() throws IOException {
        super(Controller.class, "v1", "blog");
    }

    /**
     * Main application server
     */
    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    /**
     * Custom App routes
     */
    @Override
    public HashMap<String, String> appRoutes() {
        HashMap<String, String> routes = new HashMap<>();

        routes.put("test", "Controller::test");
        routes.put("sample", "Controller::sample");
        routes.put("abba", "Controller::abba");

        return routes;
    }

}