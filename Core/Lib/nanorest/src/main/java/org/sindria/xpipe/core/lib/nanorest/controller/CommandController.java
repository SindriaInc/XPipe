package org.sindria.xpipe.core.lib.nanorest.controller;

import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.request.Request;
import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

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