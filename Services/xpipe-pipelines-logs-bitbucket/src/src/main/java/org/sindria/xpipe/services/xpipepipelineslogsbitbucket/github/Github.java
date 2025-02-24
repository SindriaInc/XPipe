package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github;

import org.json.JSONObject;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Repository;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Variable;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Workflow;

import java.util.HashMap;

public class Github {

    // Repositories

    public static Object listOrganizationRepositories(String organization){
        return GithubHelper.get("/orgs/" + organization + "/repos");
    }

    public static Object getARepository(String organization, String repository){
        return GithubHelper.get("/repos/" + organization + "/" + repository);
    }

    // Basic create repository payload, see model for additional parameters
    public static JSONObject createAnOrganizationRepository(String organization, String name) {

        Repository payload = new Repository(name);

        return GithubHelper.post("/orgs/" + organization + "/repos", payload.serialize());

    }

    // Basic update repository payload, see model for additional parameters
    public static JSONObject updateARepository(String organization, String name) {

        Repository payload = new Repository(name);

        return GithubHelper.patch("/orgs/" + organization + "/repos", payload.serialize());
    }

    public static JSONObject deleteARepository(String organization, String name) {
        return GithubHelper.delete("/repos/" + organization + "/" + name);
    }


    // Actions/Variables

    public static Object listRepositoryVariable(String organization, String repoName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/variables");
    }
    public static JSONObject createARepositoryVariable(String organization, String repoName, String variableName, String variableValue) {

        Variable payload = new Variable(variableName, variableValue);
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/variables", payload.serialize());
    }

    public static Object getARepositoryVariable(String organization, String repoName, String variableName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName);
    }

    public static JSONObject updateARepositoryVariable(String organization, String repoName, String variableName, String variableValue) {

        Variable payload = new Variable(variableName, variableValue);

        return GithubHelper.patch("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName, payload.serialize());
    }

    public static JSONObject deleteARepositoryVariable(String organization, String repoName, String variableName) {
        return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName);
    }

    // Actions/Artifacts

    public static Object listArtifactsForARepository(String organization, String repoName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/artifacts");
    }

    public static Object getAnArtifact(String organization, String repoName, String artifactId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId);
    }

    public static Object deleteAnArtifact(String organization, String repoName, String artifactId) {
        return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId);
    }

    public static Object listWorkflowRunArtifact(String organization, String repoName, String workflowRunId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + workflowRunId + "/artifacts");
    }


//TODO: implement
//    public static Object downloadAnArtifact(String organization, String repoName, String artifactId) {
//        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId + "/zip");
//    }


    // Actions/Workflows

    public static Object listRepositoryWorkflows(String organization, String repoName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/workflows");
    }

    public static Object getAWorkflow(String organization, String repoName, String workflowId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/workflows/" + workflowId);
    }

    public static JSONObject enableAWorkflow(String organization, String repoName, String workflowId) {
        return GithubHelper.put("/repos/" + organization + "/" + repoName + "/actions/workflows/" + workflowId + "/enable", "{}");
    }

    public static JSONObject disableAWorkflow(String organization, String repoName, String workflowId) {
        return GithubHelper.put("/repos/" + organization + "/" + repoName + "/actions/workflows/" + workflowId + "/disable", "{}");
    }

    public static JSONObject createAWorkflowDispatchEvent(String organization, String repoName, String workflowId, String ref, HashMap<String,String> inputs) {
        Workflow payload = new Workflow(ref, inputs);
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/workflows/" + workflowId + "/dispatches", payload.serialize());
    }

    // Actions/Workflow jobs

    public static Object getAJobForAWorkflowRun(String organization, String repoName, String jobId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/jobs/" + jobId);
    }

    //TODO: implement
//    public static Object downloadJobLogsForAWorkflowRun(String organization, String repoName, String jobId) {
//        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/jobs/" + jobId + "/logs");
//    }



}
