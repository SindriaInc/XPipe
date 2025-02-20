package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket;

import org.json.JSONObject;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.Helper;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.CreateRepository;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.CreateOrUpdateAVariableForARepository;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.bitbucket.models.UpdateConfiguration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public static JSONObject updateConfiguration(String workspace, String repoSlug) {

        UpdateConfiguration payload = new UpdateConfiguration(false);

        return Helper.put("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config", payload.serialize());
    }

    public static JSONObject listVariablesForARepository(String workspace, String repoSlug){
        return Helper.get("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables");
    }

    public static JSONObject createAVariableForARepository(String workspace, String repoSlug, String key, String value, boolean secured) {

        CreateOrUpdateAVariableForARepository payload = new CreateOrUpdateAVariableForARepository(key, value, secured);

        return Helper.post("/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables", payload.serialize());
    }

    public static JSONObject getAVariableForARepository(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/"  + uuid;


        return Helper.get(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

    public static JSONObject updateAVariableForARepository(String workspace, String repoSlug, String uuid, String key, String value, boolean secured) {

        CreateOrUpdateAVariableForARepository payload = new CreateOrUpdateAVariableForARepository(key, value, secured);

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/" + uuid;

        return Helper.put(URLEncoder.encode(uri, StandardCharsets.UTF_8), payload.serialize());
    }

    public static JSONObject deleteAVariableForARepository(String workspace, String repoSlug, String uuid) {

        String uri = "/repositories/" + workspace + "/" + repoSlug + "/pipelines_config/variables/" + uuid;

        return Helper.delete(URLEncoder.encode(uri, StandardCharsets.UTF_8));
    }

}
