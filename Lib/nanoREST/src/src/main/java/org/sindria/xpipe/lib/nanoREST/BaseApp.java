package org.sindria.xpipe.lib.nanoREST;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.sindria.xpipe.lib.nanoREST.config.AppConfig;
import org.sindria.xpipe.lib.nanoREST.handlers.*;

// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public abstract class BaseApp<T> extends RouterNanoHTTPD {

    /**
     * Controller Class
     */
    protected Class<T> controller;

//    /**
//     * app
//     */
//    public static AppConfig app;

    /**
     * apiVersion
     */
    public static String apiVersion;

    /**
     * serviceName
     */
    public static String serviceName;

    /**
     * appRoutes
     */
    public static HashMap<String, String> appRoutes;

    /**
     * UriRouter
     */
    private final RouterNanoHTTPD.UriRouter router = new RouterNanoHTTPD.UriRouter();

    /**
     * BaseApp constructor
     */
    public BaseApp(Class<T> typeController, String apiVersion, String serviceName) throws IOException {
        //super(8080);
        super(AppConfig.getInstance().getPort());
        //super(AppConfig.config.getNanorest().getNanohttpd().getPort());
        this.controller = typeController;
        BaseApp.apiVersion = apiVersion;
        BaseApp.serviceName = serviceName;
        BaseApp.appRoutes = this.appRoutes();
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    /**
     * Register app routes
     */
    @Override
    public void addMappings() {
        router.setNotImplemented(NotImplementedHandler.class);
        router.setNotFoundHandler(Error404UriHandler.class);
        addRoute("/", nanoRESTIndexHandler.class);
        addRoute("/index.html", nanoRESTIndexHandler.class);

        addRoute("/api/"+ BaseApp.apiVersion+"/"+ BaseApp.serviceName, this.controller);

        for (String key : BaseApp.appRoutes.keySet()) {
            addRoute("/api/"+ BaseApp.apiVersion+"/"+ BaseApp.serviceName+"/"+key, this.controller);
        }
    }

    /**
     * Default App routes
     */
    public HashMap<String, String> appRoutes() {
        HashMap<String, String> routes = new HashMap<>();
        routes.put("test", "this.controller::test");
        return routes;
    }
}
