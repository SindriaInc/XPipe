package org.sindria.xpipe.services.xpipepipelineslogsbitbucket;

import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.Bitbucket;
import org.sindria.xpipe.lib.nanoREST.controllers.*;
import org.sindria.xpipe.lib.nanoREST.requests.*;

import java.io.IOException;
import java.time.chrono.HijrahEra;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.response.RestResponse;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.pipeline.PipelineVariable;

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


//
//    public RestResponse sample(Request request) {
//
//        // TODO: get createdAt by external HTTP request - maybe by get param
//        JSONObject competitions = this.service.getCompetitions("07/04/2021");
//
//        var competitionsCleaned = Helper.cleanCompetitions(competitions);
//
//        logger.info("Launching jobs");
//
//        this.jobDispatcher.submitJob(new Job("Task 1", 2));
//        this.jobDispatcher.submitJob(new TestJob("Task 2", 1));
//        this.jobDispatcher.submitJob(new Job("Task 3", 3));
//
//        this.jobDispatcher.scheduleRecurringJob("* * * * *", new Job("Cron Recurring Task", 2));
//
//
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("competitions", competitions);
//
//        return this.sendResponse("ok", 200, data);
//    }


    // questo lo usi per testare il singolo metodo (uno alla volta)
    public RestResponse bitbucket(Request request) throws IOException {

        // Test list repositories
//        JSONObject bitbucket1 = Bitbucket.listRepositories("xpipe-pipelines");

        // Test delete a variable for a repository
        JSONObject bitbucket2 = Bitbucket.getAStepOfAPipeline("xpipe-pipelines", "xp-orchestrator-pipeline", "{bc1a46b5-7353-4228-9f80-44945fb56025}", "{44b5cc30-9613-4b80-9471-2c0871c0d449}");


        HashMap<String, Object> data = new HashMap<>();
//        data.put("bitbucket1", bitbucket1);
        data.put("bitbucket2", bitbucket2);

        return this.sendResponse("ok", 200, data);
    }

    public RestResponse triggerPipeline(Request request) throws IOException {

        ArrayList<PipelineVariable> variables = new ArrayList<>();
        PipelineVariable xPipeTicketId =  new PipelineVariable("XPIPE_TICKET_ID", "#{ticket.id}", false);
        PipelineVariable xPipeTicketStateName = new PipelineVariable("XPIPE_TICKET_STATE_NAME", "#{ticket.state.name}", false);
        PipelineVariable xPipeTicketPriorityName = new PipelineVariable("XPIPE_TICKET_PRIORITY_NAME", "#{ticket.priority.name}", false);
        variables.add(xPipeTicketId);
        variables.add(xPipeTicketStateName);
        variables.add(xPipeTicketPriorityName);


        JSONObject triggerPipeline = Bitbucket.triggerPipeline("xpipe-pipelines", "xp-orchestrator-pipeline",  "branch", "master", variables);
//        JSONObject stopPipeline = Bitbucket.stopPipeline("xpipe-pipelines", "xp-orchestrator-pipeline", "{319d29ef-120b-460a-a1b6-44c127a93f38}", "branch", "master", variables);


        HashMap<String, Object> data = new HashMap<>();
        data.put("trigger_pipeline", triggerPipeline);
//        data.put("stop_pipeline", stopPipeline);

        return this.sendResponse("ok", 200, data);
    }




    // questo lo usi per implementare la batteria di test completa con almeno un esempio di utilizzo di ogni funzione
    public RestResponse bitbucketTestCases(Request request) throws IOException {

        HashMap<String, Object> data = new HashMap<>();

        return this.sendResponse("ok", 200, data);
    }



}