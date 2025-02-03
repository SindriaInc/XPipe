package org.sindria.xpipe.lib.nanoREST.controllers;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.requests.Request;

import java.util.HashMap;

public class CommandController extends BaseController {

    /**
     * Controller constructor
     */
    public CommandController(Class typeController) {
        super(typeController);
    }

    public HashMap<String, Object> test(Request request) {
        HashMap<String, Object> data = new HashMap<>();
        return this.sendResponse("Test command", 201, data);
        //return new JSONObject("{\"test\": [] }");
    }
}