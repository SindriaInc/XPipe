package org.sindria.xpipe.services.xpipepipelineslogsbitbucket;

import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.Bitbucket;
import org.sindria.xpipe.lib.nanoREST.controllers.*;
import org.sindria.xpipe.lib.nanoREST.requests.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.response.RestResponse;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.Variable;

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


    // questo lo usi per testare il singolo metodo (uno alla volta)
    public RestResponse bitbucket(Request request) throws IOException {

        // Test list repositories
//        JSONObject bitbucket1 = Bitbucket.listRepositories("xpipe-pipelines");

        // Test delete a variable for a repository
        JSONObject bitbucket2 = Bitbucket.createVariableForAWorkspace("xpipe-pipelines", "pippo", "baudo", false);


        HashMap<String, Object> data = new HashMap<>();
//        data.put("bitbucket1", bitbucket1);
        data.put("bitbucket2", bitbucket2);

        return this.sendResponse("ok", 200, data);
    }

    public RestResponse triggerPipeline(Request request) throws IOException {

        ArrayList<Variable> variables = new ArrayList<>();
        Variable xPipeTicketId =  new Variable("XPIPE_TICKET_ID", "#{ticket.id}", false);
        Variable xPipeTicketStateName = new Variable("XPIPE_TICKET_STATE_NAME", "#{ticket.state.name}", false);
        Variable xPipeTicketPriorityName = new Variable("XPIPE_TICKET_PRIORITY_NAME", "#{ticket.priority.name}", false);
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