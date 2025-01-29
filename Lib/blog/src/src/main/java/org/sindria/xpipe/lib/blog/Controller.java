package org.sindria.xpipe.lib.blog;

import fi.iki.elonen.NanoHTTPD;
import org.sindria.xpipe.lib.nanoREST.controllers.*;
import org.sindria.xpipe.lib.nanoREST.helpers.BaseHelper;
import org.sindria.xpipe.lib.nanoREST.requests.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Controller extends TestController<Controller> {

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
    public JSONObject test(Request request) {

        var fieldExamples = new HashMap<String, String>();
        fieldExamples.put("email", "required|email");
        fieldExamples.put("eta", "text");
        //request.validator.validate(fieldExamples);


//        var debug = BaseHelper.rd(request);
//
//        System.out.println(debug);
//
//        return debug;

//        System.out.println("Service Content:");
//        System.out.println(request.getContent());
//        System.out.println("Service Content string:");
//        System.out.println(request.getContent().toString());
//        System.out.println("---------------");

        //String test = request.input("name");
        //System.out.println(test);
        //System.out.println(request.content());

        //logger.info("This is an info message.");
//        logger.warning("This is a warning message.");
//        logger.severe("This is a severe error message.");
//        logger.debug("This is a debug message.");


        JSONObject data = new JSONObject();
        data.put("headers", request.getHeaders());
        data.put("cookies", request.getCookies());
        data.put("method", request.getMethod());
        data.put("uri", request.requestUri);
        data.put("user-agent", request.userAgent());
        data.put("query", request.query);
        data.put("content-type", request.getContentType());
        //data.put("content", request.content());
        return data;


//        return new JSONObject("{\"test custom abba\": [] }");
    }



    public JSONObject sample(Request request) {

        // TODO: get createdAt by external HTTP request - maybe by get param
        JSONObject competitions = this.service.getCompetitions("07/04/2021");

        var competitionsCleaned = Helper.cleanCompetitions(competitions);

        logger.info("This is an info message");
        //System.out.println("Sticazzi");

        //System.out.println(competitionsCleaned);

//        for(int i = 0; i < competitionsCleaned.length(); i++) {
//            JSONObject entry = competitionsCleaned.getJSONObject(i);
//            System.out.println(entry);
//        }


        //Iterator<Boolean> objectIterator =  competitionsCleaned.iterator();

//        for(int i = 0; competitionsCleaned.length() < i; i++) {
//            JSONObject object = (JSONObject) competitionsCleaned.get(i);
//            System.out.println(object);
//        }


        // TODO: iterate all competitions and match only citta: Arzachena and competition status
//        String current = "16472";
//
//        String origin = "https://myfit.federtennis.it/";
//
//        JSONObject data = new JSONObject();
//        data.put("competitionId", current);
//        data.put("phaseId", "D5560D41-ADCB-454E-9495-FD503F26E192");
//        data.put("Guid", "NULL");
//
//        var result = Helper.post("/api/v3/tournament/bracket", origin, data.toString());

//        if (result == null) {
//            return "result is null";
//        }

        return competitions;
    }




}