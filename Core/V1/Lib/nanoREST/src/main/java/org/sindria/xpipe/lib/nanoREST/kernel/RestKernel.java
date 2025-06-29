package org.sindria.xpipe.lib.nanoREST.kernel;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.sindria.xpipe.lib.nanoREST.config.AppConfig;
import org.sindria.xpipe.lib.nanoREST.handlers.FrontHandler;
import org.sindria.xpipe.lib.nanoREST.handlers.Error404Handler;

// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public abstract class RestKernel extends RouterNanoHTTPD {

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
     * RestKernel void
     */
    public RestKernel() throws IOException {
        super(8080);
    }

    /**
     * RestKernel constructor v1 hardcoded
     */
    public RestKernel(Class typeController, String apiVersion, String serviceName) throws IOException {
        super(AppConfig.getInstance().getPort());
        this.typeController = typeController;
        RestKernel.apiVersion = apiVersion;
        RestKernel.serviceName = serviceName;
        RestKernel.appRoutes = this.appRoutes();
        addMappings();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:" + AppConfig.config.getNanorest().getNanohttpd().getPort() + "\n");
    }

    /**
     * RestKernel constructor v2 config
     */
    public RestKernel(Class typeController) throws IOException {
        super(AppConfig.getInstance().getPort());
        this.typeController = typeController;
        RestKernel.apiVersion = AppConfig.config.getNanorest().getApplication().getVersion();
        RestKernel.serviceName = AppConfig.config.getNanorest().getApplication().getName();
        RestKernel.appRoutes = this.appRoutes();
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

        addRoute("/api/"+ RestKernel.apiVersion+"/"+ RestKernel.serviceName, this.typeController);

        for (String key : RestKernel.appRoutes.keySet()) {
            addRoute("/api/"+ RestKernel.apiVersion+"/"+ RestKernel.serviceName+"/"+key, this.typeController);
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
