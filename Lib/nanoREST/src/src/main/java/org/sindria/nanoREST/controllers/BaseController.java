package org.sindria.nanoREST.controllers;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;
import org.sindria.nanoREST.BaseApp;
import org.sindria.nanoREST.requests.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public abstract class BaseController<T> extends RouterNanoHTTPD.GeneralHandler {

    /**
     * Controller Class
     */
    protected Class<T> controller;

    /**
     * apiVersion
     */
    public String apiVersion;

    /**
     * serviceName
     */
    public String serviceName;

    /**
     * reservedUri
     */
    protected String reservedUri;

    /**
     * BaseController constructor
     */
    public BaseController(Class<T> typeController) {
        this.controller = typeController;
        this.apiVersion = BaseApp.apiVersion;
        this.serviceName = BaseApp.serviceName;
        this.reservedUri = "api/" + apiVersion + "/" + serviceName;
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    /**
     * Wrapper call to action
     */
    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        JSONObject result;
        try {
            result = this.callControllerAction(uriResource, urlParams, session);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            result = new JSONObject("{\"resource\":{\"message\":\"Fatal error in wrapper call to action\", \"error\":\""+e+"\"}}");
        }

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), result.toString());
    }

    /**
     * Call controller action
     */
    public JSONObject callControllerAction(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String currentUri = uriResource.getUri();

        if (currentUri.equals(this.reservedUri)) {
            return new JSONObject("{\"resource\":{\"message\":\"This route is reserved for crud controller\"}}");
        }

        String methodMatched = this.matchUriMethod(currentUri);

        if (methodMatched == null) {
            return new JSONObject("{\"resource\":{\"message\":\"This method is not implemented yet\"}}");
        }


        try {
            String controllerName = this.controller.getSimpleName();

            if (controllerName.equals("Controller")) {
                Method getInstance = this.controller.getDeclaredMethod("getInstance");
                Object instance = getInstance.invoke(null);

                var request = new Request(uriResource,  urlParams, session);

                Method methodCall = instance.getClass().getMethod(methodMatched, Request.class);
                return (JSONObject) methodCall.invoke(instance, request);
            }
            return new JSONObject("{\"resource\":{\"message\":\"Controller class unsupported, sorry\"}}");
        } catch(Exception e) {
            System.out.println("Wrapper exception: " + e);
            System.out.println("Underlying exception: " + e.getCause());
            return new JSONObject("{\"resource\":{\"message\":\"Fatal error during callControllerAction() in BaseController\", \"error\":\""+e+"\", \"cause\":\""+e.getCause()+"\"}}");
        }
    }

    /**
     * Match method for current uri
     */
    private String matchUriMethod(String currentUri) {
        String methodName = null;

        for (String key : BaseApp.appRoutes.keySet()) {

            String uriPath = this.reservedUri + "/" + key;

            if (currentUri.equals(uriPath)) {

                String checkMethod = BaseApp.appRoutes.get(key);

                if (checkMethod.contains("Controller::")) {
                    methodName = checkMethod.replace("Controller::", "");
                } else if (checkMethod.contains("this.controller::")) {
                    methodName = checkMethod.replace("this.controller::", "");
                } else {
                    methodName = BaseApp.appRoutes.get(key);
                }
            }
        }
        return methodName;
    }
}