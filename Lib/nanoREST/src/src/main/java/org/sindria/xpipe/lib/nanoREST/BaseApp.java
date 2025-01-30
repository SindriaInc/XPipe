package org.sindria.xpipe.lib.nanoREST;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.config.AppConfig;
import org.sindria.xpipe.lib.nanoREST.handlers.FrontHandler;
import org.sindria.xpipe.lib.nanoREST.handlers.Error404Handler;
import org.sindria.xpipe.lib.nanoREST.router.Router;

// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public abstract class BaseApp extends RouterNanoHTTPD {

    /**
     * Controller Class
     */
    protected Class typeController;

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
    private final RouterNanoHTTPD.UriRouter nanoRestRouter = new RouterNanoHTTPD.UriRouter();
    //private final UriRouter nanoRestRouter = new Router();

    /**
     * BaseApp constructor v1 hardcoded
     */
    public BaseApp(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(AppConfig.getInstance().getPort());
        this.typeController = typeController;
        BaseApp.apiVersion = apiVersion;
        BaseApp.serviceName = serviceName;
        BaseApp.appRoutes = this.appRoutes();
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:" + AppConfig.config.getNanorest().getNanohttpd().getPort() + "\n");
    }

    /**
     * BaseApp constructor v2 config
     */
    public BaseApp(Class typeController) throws IOException {
        super(AppConfig.getInstance().getPort());
        this.typeController = typeController;
        BaseApp.apiVersion = AppConfig.config.getNanorest().getApplication().getVersion();
        BaseApp.serviceName = AppConfig.config.getNanorest().getApplication().getName();
        BaseApp.appRoutes = this.appRoutes();
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:" + AppConfig.config.getNanorest().getNanohttpd().getPort() + "\n");
    }

    /**
     * Register app routes
     */
    @Override
    public void addMappings() {
        //this.nanoRestRouter.setNotImplemented(NotImplementedHandler.class);

        //this.router.setNotFoundHandler(Error404Handler.class);
        this.nanoRestRouter.setNotFoundHandler(Error404Handler.class);
        //System.out.println("Debug router:");
        //System.out.println(new JSONObject(this.nanoRestRouter));
        //System.out.println(new JSONObject(this.router));

        addRoute("/", FrontHandler.class);
        addRoute("/index.html", FrontHandler.class);

        addRoute("/api/"+ BaseApp.apiVersion+"/"+ BaseApp.serviceName, this.typeController);

        for (String key : BaseApp.appRoutes.keySet()) {
            addRoute("/api/"+ BaseApp.apiVersion+"/"+ BaseApp.serviceName+"/"+key, this.typeController);
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
