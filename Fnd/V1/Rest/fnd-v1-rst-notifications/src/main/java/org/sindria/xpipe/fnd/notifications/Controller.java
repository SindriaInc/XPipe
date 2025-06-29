package org.sindria.xpipe.fnd.notifications;

import fi.iki.elonen.NanoHTTPD;
import org.sindria.xpipe.fnd.notifications.jobs.TestJob;
import org.sindria.xpipe.lib.nanoREST.controllers.*;
import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;
import org.sindria.xpipe.lib.nanoREST.job.Job;
import org.sindria.xpipe.lib.nanoREST.requests.*;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.response.RestResponse;

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
        //data.put("content", request.content());
        //return data;

        HashMap<String, Object> data = new HashMap<>();
        data.put("request", requestData);

        return this.sendResponse("ok", 200, data);
    }



    public RestResponse sample(Request request) {

        // TODO: get createdAt by external HTTP request - maybe by get param
        JSONObject competitions = this.service.getCompetitions("07/04/2021");

        var competitionsCleaned = Helper.cleanCompetitions(competitions);

        logger.info("Launching jobs");

        this.jobDispatcher.submitJob(new Job("Task 1", 2));
        this.jobDispatcher.submitJob(new TestJob("Task 2", 1));
        this.jobDispatcher.submitJob(new Job("Task 3", 3));


        HashMap<String, Object> data = new HashMap<>();
        data.put("competitions", competitions);



        return this.sendResponse("ok", 200, data);
    }




}