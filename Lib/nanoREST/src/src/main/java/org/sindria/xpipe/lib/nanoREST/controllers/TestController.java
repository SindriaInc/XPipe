package org.sindria.xpipe.lib.nanoREST.controllers;

import java.util.HashMap;
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

    public HashMap<String, Object> test(Request request) {
        //return new JSONObject("{\"test\": [] }");
        HashMap<String, Object> data = new HashMap<>();
        return this.sendResponse("Test method", 200, data);
    }
}