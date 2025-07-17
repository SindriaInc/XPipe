package org.sindria.xpipe.core.lib.nanorest.handler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.sindria.xpipe.core.lib.nanorest.action.Action;
import org.sindria.xpipe.core.lib.nanorest.logger.Logger;
import org.sindria.xpipe.core.lib.nanorest.registry.ActionRegistry;
import org.sindria.xpipe.core.lib.nanorest.request.Request;
import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

import java.util.HashMap;
import java.util.Map;

public class FrontHandler extends RouterNanoHTTPD.GeneralHandler {

    private final Logger logger = Logger.getInstance();

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        String path = uriResource.getUri();

        logger.debug("FrontHandler handling path: " + path);

        Action action = ActionRegistry.get(path);
        if (action == null) {
            return buildResponse(new RestResponse(404, false, "Action not found: " + path, new HashMap<>()));
        }

        try {
            Request request = new Request(uriResource, urlParams, session);
            RestResponse result = action.execute(request);
            return buildResponse(result);
        } catch (Exception e) {
            logger.logException("Error executing action for path: " + path, e);
            return buildResponse(new RestResponse(500, false, "Internal server error", new HashMap<>()));
        }
    }

    private NanoHTTPD.Response buildResponse(RestResponse result) {
        NanoHTTPD.Response.IStatus status = getStatus(result.getCode());
        return NanoHTTPD.newFixedLengthResponse(status, "application/json", result.serialize().toString());
    }

    private NanoHTTPD.Response.IStatus getStatus(int code) {
        return switch (code) {
            case 200 -> NanoHTTPD.Response.Status.OK;
            case 201 -> NanoHTTPD.Response.Status.CREATED;
            case 400 -> NanoHTTPD.Response.Status.BAD_REQUEST;
            case 401 -> NanoHTTPD.Response.Status.UNAUTHORIZED;
            case 403 -> NanoHTTPD.Response.Status.FORBIDDEN;
            case 404 -> NanoHTTPD.Response.Status.NOT_FOUND;
            case 500 -> NanoHTTPD.Response.Status.INTERNAL_ERROR;
            default -> NanoHTTPD.Response.Status.OK;
        };
    }
}
