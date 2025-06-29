package org.sindria.xpipe.lib.nanoREST.bitbucket;

import org.sindria.xpipe.lib.nanoREST.bitbucket.models.CreateRepository;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.UpdateConfiguration;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.Variable;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.pipeline.Pipeline;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.pipeline.PipelineTarget;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.schedule.CreateSchedule;
import org.sindria.xpipe.lib.nanoREST.bitbucket.models.schedule.ScheduleTarget;

import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Bitbucket {

    // Repository

    public static JSONObject listRepositories(String workspace){
        return Helper.get("/repositories/" + workspace);
    }

    public static JSONObject createRepository(String workspace, String repoSlug, String projectKey, boolean isPrivate){

        HashMap<String, String> project = new HashMap<>();
        project.put("key", projectKey);

        CreateRepository payload = new CreateRepository("git", isPrivate, project);

        return Helper.post("/repositories/" + workspace + "/" + repoSlug, payload.serialize());
    }

    public static JSONObject getConfiguration(String workspace, String repoSlug){
        return Helper.get("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config");
    }

    public static JSONObject updateConfiguration(String workspace, String repoSlug, boolean enabled) {

        UpdateConfiguration payload = new UpdateConfiguration(enabled);

        return Helper.put("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config", payload.serialize());
    }

    public static JSONObject listVariablesForARepository(String workspace, String repoSlug){
        return Helper.get("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables");
    }

    public static JSONObject createAVariableForARepository(String workspace, String repoSlug, String key, String value, boolean secured) {

        Variable payload = new Variable(key, value, secured);

        return Helper.post("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables", payload.serialize());
    }

    public static JSONObject getAVariableForARepository(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/"  + uuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject updateAVariableForARepository(String workspace, String repoSlug, String uuid, String key, String value, boolean secured) {

        Variable payload = new Variable(key, value, secured);

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/" + uuid;

        return Helper.put(URLEncoder.encode(uri, StandardCharsets.UTF_8), payload.serialize());
    }

    public static JSONObject deleteAVariableForARepository(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/" + uuid;

        return Helper.delete(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }



    // Schedules

    public static JSONObject listSchedules(String workspace, String repoSlug){
        return Helper.get("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules");
    }

    public static JSONObject getSchedule(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules/"  + uuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject listExecutionsOfASchedule(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules/"  + uuid + "/executions";


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject createSchedule(String workspace, String repoSlug, String selectorType, String targetRefName, String targetRefType, boolean enabled, String cronPattern) {

        HashMap<String, String> targetSelector = new HashMap<>();
        targetSelector.put("type", selectorType);

        ScheduleTarget scheduleTarget = new ScheduleTarget(targetSelector, targetRefName, targetRefType);

        CreateSchedule payload = new CreateSchedule(scheduleTarget, enabled, cronPattern);


        return Helper.post("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules", payload.serialize());
    }

    public static JSONObject updateSchedule(String workspace, String repoSlug, String uuid, boolean enabled) {

        UpdateConfiguration payload = new UpdateConfiguration(enabled);

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules/" + uuid;

        return Helper.put(URLEncoder.encode(uri, StandardCharsets.UTF_8), payload.serialize());
    }

    public static JSONObject deleteSchedule(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/schedules/" + uuid;

        return Helper.delete(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }


    // Pipelines

    public static JSONObject listPipelines(String workspace, String repoSlug){
        return Helper.get("/repositories/" + workspace + "/" + repoSlug + "/pipelines");
    }

    public static JSONObject getPipeline(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject getPipelineArtifacts(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid + "/artifacts";


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject listStepsForAPipeline(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid + "/steps";


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject getAStepOfAPipeline(String workspace, String repoSlug, String uuid, String stepUuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid + "/steps/" + stepUuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    // It does not work at the moment
    public static JSONObject getLogFileForAStep(String workspace, String repoSlug, String uuid, String stepUuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid + "/steps/" + stepUuid + "/log";


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    // It does not work at the moment
    public static JSONObject getLogFileForAStep(String workspace, String repoSlug, String uuid, String stepUuid, String logUuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines/"  + uuid + "/steps/" + stepUuid + "/logs" + logUuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject triggerPipeline(String workspace, String repoSlug, String refType, String refName, ArrayList<Variable> variables) {

        PipelineTarget target = new PipelineTarget(refType, refName);

        Pipeline payload = new Pipeline(target, variables);

        return Helper.post("/repositories/" + workspace + "/" + repoSlug + "/pipelines", payload.serialize());
    }

    public static JSONObject stopPipeline(String workspace, String repoSlug, String uuid, String refType, String refName, ArrayList<Variable> variables) {

        PipelineTarget target = new PipelineTarget(refType, refName);

        Pipeline payload = new Pipeline(target, variables);

        return Helper.post("/repositories/" + workspace + "/" + repoSlug + "/pipelines" + uuid + "/stopPipeline" , payload.serialize());
    }


    // Workspaces

    public static JSONObject listVariablesForAWorkspace(String workspace){
        return Helper.get("/workspaces/" + workspace + "/pipelines-config/variables");
    }

    public static JSONObject createVariableForAWorkspace(String workspace, String key, String value, boolean secured) {

        Variable payload = new Variable(key, value, secured);

        return Helper.post("/workspaces/" + workspace + "/pipelines-config/variables", payload.serialize());
    }

    public static JSONObject getVariableForAWorkspace(String workspace, String uuid){

        String uri = "/workspaces/" + workspace + "/pipelines-config/variables" + uuid;

        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject updateVariableForAWorkspace(String workspace, String uuid, String key, String value, boolean secured) {

        Variable payload = new Variable(key, value, secured);

        String uri = "/workspaces/" + workspace + "/pipelines-config/variables" + uuid;

        return Helper.put(URLEncoder.encode(uri, StandardCharsets.UTF_8), payload.serialize());
    }

    public static JSONObject deleteVariableForAWorkspace(String workspace, String uuid) {

        String uri = "/workspaces/" + workspace + "/pipelines-config/variables" + uuid;

        return Helper.delete(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    // Projects
    public static JSONObject listProjects(String workspace){
        return Helper.get("/workspaces/" + workspace + "/projects");
    }

}
