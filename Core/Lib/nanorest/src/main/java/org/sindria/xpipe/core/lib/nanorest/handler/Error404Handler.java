package org.sindria.xpipe.core.lib.nanorest.handler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;

import java.util.Map;

public class Error404Handler extends RouterNanoHTTPD.DefaultHandler {

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public String getText() {
        JSONObject result;
        result = new JSONObject("{\"resource\":{\"message\":\"Error 404: the requested page doesn't exist.\"}");
        return result.toString();
    }

//    @Override
//    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
//        JSONObject result;
//        result = new JSONObject("{\"resource\":{\"message\":\"Error 404: the requested page doesn't exist.\"}");
//        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), result.toString());
//    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.NOT_FOUND;
    }



}
