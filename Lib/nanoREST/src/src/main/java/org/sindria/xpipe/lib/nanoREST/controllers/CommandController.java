package org.sindria.xpipe.lib.nanoREST.controllers;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.requests.Request;
import org.sindria.xpipe.lib.nanoREST.response.RestResponse;

import java.util.HashMap;

public class CommandController extends BaseController {

    /**
     * Controller constructor
     */
    public CommandController(Class typeController) {
        super(typeController);
    }

    public RestResponse test(Request request) {
        return this.sendResponse("Test command", 201);
        //return new JSONObject("{\"test\": [] }");
    }
}