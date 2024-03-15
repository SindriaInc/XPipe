package org.sindria.xppipelines.controllers;

import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
public class ApiController {

    /**
     * Success response
     */
    public HashMap<String, Object> sendResponse(HttpServletResponse response, String message, Integer code, HashMap<String, Object> data) {

        response.setStatus(code);

        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("message", message);

        return result;
    }


    /**
     * Error response
     */
    public HashMap<String, Object> sendError(HttpServletResponse response, String message, Integer code, HashMap<String, Object> data) {

        response.setStatus(code);

        HashMap<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", data);
        result.put("message", message);

        return result;
    }
}
