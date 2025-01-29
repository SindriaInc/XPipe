package org.sindria.xpipe.lib.nanoREST.controllers;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.requests.Request;

public class CommandController<T> extends BaseController<CommandController> {

    /**
     * Controller constructor
     */
    public CommandController(Class<T> typeController) {
        super((Class<CommandController>) typeController);
    }

    public JSONObject test(Request request) {
        return new JSONObject("{\"test\": [] }");
    }
}