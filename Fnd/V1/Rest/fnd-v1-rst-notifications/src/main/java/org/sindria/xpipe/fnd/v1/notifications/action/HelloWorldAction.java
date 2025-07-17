package org.sindria.xpipe.fnd.v1.notifications.action;

import org.sindria.xpipe.core.lib.nanorest.action.Action;
import org.sindria.xpipe.core.lib.nanorest.request.Request;
import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

import java.util.HashMap;

public class HelloWorldAction implements Action {

    @Override
    public RestResponse execute(Request request) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("message", "Hello World from Action!");
        return new RestResponse(200, true, "Success", data);
    }
}

