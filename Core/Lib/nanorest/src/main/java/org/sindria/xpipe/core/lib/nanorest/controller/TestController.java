package org.sindria.xpipe.core.lib.nanorest.controller;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.sindria.xpipe.core.lib.nanorest.request.Request;
import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

public class TestController extends BaseController {

    /**
     * Controller constructor
     */
    public TestController(Class typeController) {
        super(typeController);
    }

    public RestResponse test(Request request) {
        //return new JSONObject("{\"test\": [] }");
        HashMap<String, Object> data = new HashMap<>();
        return this.sendResponse("Test method", 200, data);
    }
}