package org.sindria.nanoREST.controllers;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;
import org.sindria.nanoREST.models.BaseModel;
import org.sindria.nanoREST.services.BaseService;

import java.util.Map;

public class CrudController<T> extends BaseController<CrudController> {

    /**
     * BaseModel
     */
    public BaseModel model;

    /**
     * Service
     */
    public BaseService service;


    /**
     * CrudController constructor
     */
    public CrudController(Class<T> typeController) {
        super(CrudController.class);
        this.service = new BaseService();
        this.model = new BaseModel();
    }


    /**
     * Get the resource
     */
    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        String currentUri = uriResource.getUri();
        Boolean check = this.checkUri(currentUri);
        if (check) {
            JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Please implement BaseController for custom routes\"}}");
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
        }

        JSONObject resource = new JSONObject("{\"resource\": [] }");

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
    }

    /**
     * Create the resource
     */
    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        String currentUri = uriResource.getUri();
        Boolean check = this.checkUri(currentUri);
        if (check) {
            JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Please implement BaseController for custom routes\"}}");
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
        }

        JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Resource created\"}}");

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
    }

    /**
     * Update the resource
     */
    @Override
    public NanoHTTPD.Response put(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        String currentUri = uriResource.getUri();
        Boolean check = this.checkUri(currentUri);
        if (check) {
            JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Please implement BaseController for custom routes\"}}");
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
        }


        JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Resource updated\"}}");

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
    }

    /**
     * Delete the resource
     */
    @Override
    public NanoHTTPD.Response delete(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        String currentUri = uriResource.getUri();
        Boolean check = this.checkUri(currentUri);
        if (check) {
            JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Please implement BaseController for custom routes\"}}");
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
        }

        JSONObject resource = new JSONObject("{\"resource\":{\"message\":\"Resource deleted\"}}");

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), resource.toString());
    }


    /**
     * checkUri
     */
    private Boolean checkUri(String currentUri) {
        return !currentUri.equals(this.reservedUri);
    }
}
