package org.sindria.xpipe.services.xpipepipelineslogsbitbucket;

import org.sindria.xpipe.lib.nanoREST.kernel.RestKernel;

import java.io.IOException;
import java.util.HashMap;

public class App extends RestKernel {

    /**
     * App constructor
     */
    protected App() throws IOException {
        //super(Controller.class, "v1", "blog");
        super(Controller.class);
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