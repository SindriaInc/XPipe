package org.sindria.nanoREST.controllers;

import java.util.Map;
import org.json.JSONObject;
import org.sindria.nanoREST.requests.Request;

public class TestController<T> extends BaseController<TestController> {

    /**
     * Controller constructor
     */
    public TestController(Class<T> typeController) {
        super((Class<TestController>) typeController);
    }

    public JSONObject test(Request request) {
        return new JSONObject("{\"test\": [] }");
    }
}