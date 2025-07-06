package org.sindria.xpipe.fnd.v1.notifications;

import fi.iki.elonen.NanoHTTPD;
import org.sindria.xpipe.fnd.notifications.jobs.BellJob;
import org.sindria.xpipe.fnd.notifications.jobs.TestJob;
import org.sindria.xpipe.core.lib.nanorest.controllers.*;
import org.sindria.xpipe.core.lib.nanorest.debugger.Debugger;
import org.sindria.xpipe.core.lib.nanorest.helpers.BaseHelper;
import org.sindria.xpipe.core.lib.nanorest.job.Job;
import org.sindria.xpipe.core.lib.nanorest.requests.*;
import org.sindria.xpipe.core.lib.nanorest.serializers.JsonSerializer;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.sindria.xpipe.core.lib.nanorest.response.RestResponse;

public class Controller extends TestController {

    /**
     * Service
     */
    public Service service;


    /**
     * Controller constructor
     */
    public Controller() {
        super(Controller.class);
        this.service = new Service();
    }

    /**
     * Controller sigleton
     */
    private static Controller INSTANCE;

    /**
     * Controller instance
     */
    public static Controller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }

    /**
     * Test custom method
     */
    public RestResponse test(Request request) {

        var fieldExamples = new HashMap<String, String>();
        fieldExamples.put("email", "required|email");
        fieldExamples.put("eta", "text");
        //request.validator.validate(fieldExamples);


        JSONObject requestData = new JSONObject();
        requestData.put("headers", request.getHeaders());
        requestData.put("cookies", request.getCookies());
        requestData.put("method", request.getMethod());
        requestData.put("uri", request.requestUri);
        requestData.put("user-agent", request.userAgent());
        requestData.put("query", request.query);
        requestData.put("content-type", request.getContentType());
        requestData.put("content", request.getContent());
        //return data;

        HashMap<String, Object> data = new HashMap<>();
        data.put("request", requestData);

        return this.sendResponse("ok", 200, data);
    }


    public RestResponse handle(Request request) {

        Gson gson = new Gson();

        System.out.println("Debug request getContent: ");
        String rawJson = request.getContent();
        // php dump(); equivalent
        Debugger.dump(rawJson);

        Payload payload = null;

        try {
            payload = gson.fromJson(rawJson, Payload.class);
            System.out.println("Debug loaded payload: ");
            Debugger.dump(payload);
            System.out.println("Deserialized Payload: " + payload.getChannel());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return this.sendResponse("Invalid JSON", 400, null);
        }

        logger.info("Launching jobs");

        try {
            this.service.handle(payload);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("result", payload);

        return this.sendResponse("ok", 200, data);
    }



}