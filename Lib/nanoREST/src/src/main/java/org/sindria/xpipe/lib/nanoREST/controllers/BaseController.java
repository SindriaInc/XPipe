package org.sindria.xpipe.lib.nanoREST.controllers;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.BaseApp;
import org.sindria.xpipe.lib.nanoREST.logger.Logger;
import org.sindria.xpipe.lib.nanoREST.requests.*;
import org.sindria.xpipe.lib.nanoREST.response.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public abstract class BaseController extends RouterNanoHTTPD.GeneralHandler {

    /**
     * Controller Class
     */
    protected Class typeController;

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
     * logger
     */
    protected Logger logger;

    /**
     * response
     */
    private Response response;

    /**
     * BaseController constructor
     */
    public BaseController(Class typeController) {
        this.typeController = typeController;
        this.apiVersion = BaseApp.apiVersion;
        this.serviceName = BaseApp.serviceName;
        this.reservedUri = "api/" + apiVersion + "/" + serviceName;
        this.logger = Logger.getInstance();
        this.response = new Response();
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {

        switch (this.response.getStatusCode()) {
            case 101:
                return NanoHTTPD.Response.Status.SWITCH_PROTOCOL;
            case 200:
                return NanoHTTPD.Response.Status.OK;
            case 201:
                return NanoHTTPD.Response.Status.CREATED;
            case 202:
                return NanoHTTPD.Response.Status.ACCEPTED;
            case 204:
                return NanoHTTPD.Response.Status.NO_CONTENT;
            case 206:
                return NanoHTTPD.Response.Status.PARTIAL_CONTENT;
            case 301:
                return NanoHTTPD.Response.Status.REDIRECT;
            case 304:
                return NanoHTTPD.Response.Status.NOT_MODIFIED;
            case 400:
                return NanoHTTPD.Response.Status.BAD_REQUEST;
            case 401:
                return NanoHTTPD.Response.Status.UNAUTHORIZED;
            case 403:
                return NanoHTTPD.Response.Status.FORBIDDEN;
            case 404:
                return NanoHTTPD.Response.Status.NOT_FOUND;
            case 405:
                return NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED;
            case 406:
                return NanoHTTPD.Response.Status.NOT_ACCEPTABLE;
            case 408:
                return NanoHTTPD.Response.Status.REQUEST_TIMEOUT;
            case 409:
                return NanoHTTPD.Response.Status.CONFLICT;
            case 416:
                return NanoHTTPD.Response.Status.RANGE_NOT_SATISFIABLE;
            case 500:
                return NanoHTTPD.Response.Status.INTERNAL_ERROR;
            case 501:
                return NanoHTTPD.Response.Status.NOT_IMPLEMENTED;
            case 505:
                return NanoHTTPD.Response.Status.UNSUPPORTED_HTTP_VERSION;
            default:
                return NanoHTTPD.Response.Status.OK;
        }
    }

    /**
     * Wrapper call to action
     */
    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        HashMap<String, Object> result;
        try {
            result = this.callControllerAction(uriResource, urlParams, session);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            HashMap<String, Object> data = new HashMap<>();
            data.put("error", e);
            data.put("cause", e.getCause());
            result = this.sendError("Fatal error in wrapper call to action", 500, data);
        }

        assert result != null;
        int code = (int) result.get("code");
        this.response.setStatusCode(code);
        // TODO: implement serializer as immutable instance instead of result.toString()
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new JSONObject(result).toString());
    }

    /**
     * Call controller action
     */
    public HashMap<String, Object> callControllerAction(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String currentUri = uriResource.getUri();

        if (currentUri.equals(this.reservedUri)) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError("This route is reserved for crud controller", 406, data);
        }

        String methodMatched = this.matchUriMethod(currentUri);

        if (methodMatched == null) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError("This method is not implemented yet", 501, data);
        }

        try {
            String controllerName = this.typeController.getSimpleName();

            if (controllerName.equals("Controller")) {
                Method getInstance = this.typeController.getDeclaredMethod("getInstance");
                logger.debug(String.valueOf(new JSONObject(getInstance)));

                Object instance = getInstance.invoke(null);
                logger.debug(String.valueOf(new JSONObject(instance)));

                Request request = new Request(uriResource,  urlParams, session);
                logger.debug(String.valueOf(new JSONObject(request)));

                Method methodCall = instance.getClass().getMethod(methodMatched, Request.class);
                //logger.debug(String.valueOf(new JSONObject(methodCall)));

                return (HashMap<String, Object>) methodCall.invoke(instance, request);
            }
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError("Controller class unsupported, sorry", 501, data);
        } catch(NoSuchMethodException e) {
            logger.logException("NoSuchMethodException during callControllerAction() in BaseController", e);
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError("This method is not implemented yet", 501, data);
        } catch(Exception e) {
            logger.logException("Wrapper exception", e);
            logger.logException("Wrapper exception detail", e.getCause());
            e.printStackTrace();
            HashMap<String, Object> data = new HashMap<>();
            data.put("error", e);
            data.put("cause", e.getCause());
            return this.sendError("Fatal error during callControllerAction() in BaseController", 500, data);
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


    /**
     * Success response
     */
    protected HashMap<String, Object> sendResponse(String message, Integer code, HashMap<String, Object> data) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("message", message);
        result.put("code", code);

        return result;
    }


    /**
     * Error response
     */
    protected HashMap<String, Object> sendError(String message, Integer code, HashMap<String, Object> data) {

        HashMap<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", data);
        result.put("message", message);
        result.put("code", code);

        return result;
    }


}