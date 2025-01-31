package org.sindria.xpipe.lib.nanoREST.controllers;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.requests.Request;

public class CommandController extends BaseController {

    /**
     * Controller constructor
     */
    public CommandController(Class typeController) {
        super(typeController);
    }

    public JSONObject test(Request request) {
        return new JSONObject("{\"test\": [] }");
    }
}