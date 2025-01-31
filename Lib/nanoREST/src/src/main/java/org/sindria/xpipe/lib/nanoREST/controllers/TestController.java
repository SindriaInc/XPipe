package org.sindria.xpipe.lib.nanoREST.controllers;

import java.util.Map;
import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.requests.Request;

public class TestController extends BaseController {

    /**
     * Controller constructor
     */
    public TestController(Class typeController) {
        super(typeController);
    }

    public JSONObject test(Request request) {
        return new JSONObject("{\"test\": [] }");
    }
}