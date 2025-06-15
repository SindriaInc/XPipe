package org.sindria.xpipe.lib.nanoREST.github;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.github.models.Repository;
import org.sindria.xpipe.lib.nanoREST.github.models.Variable;
import org.sindria.xpipe.lib.nanoREST.github.models.Workflow;
import org.sindria.xpipe.lib.nanoREST.github.models.builder.RepositoryBuilder;

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

        Repository payload = new RepositoryBuilder(name)
                .setDescription("Un repository di test")
                .setIsPrivate(false)
                .setHasIssues(true)
                .setTeamId(42)
                .setLicenseTemplate("mit")
                .build();

        System.out.println(payload.serialize());

        return GithubHelper.post("/orgs/" + organization + "/repos", payload.serialize());

    }

    // Basic update repository payload, see model for additional parameters
    public static JSONObject updateARepository(String organization, String name) {

        Repository payload = new RepositoryBuilder(name)
                .setDescription("Un repository di test")
                .setIsPrivate(true)
                .setTeamId(42)
                .setLicenseTemplate("mit")
                .build();

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
//    public static JSONObject downloadAnArtifact(String organization, String repoName, String artifactId) {
//        return GithubHelper.downloadFile("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId + "/zip", "/tmp/" + repoName.toLowerCase() + "_" + artifactId + ".zip");
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

    public static String downloadJobLogsForAWorkflowRun(String organization, String repoName, String jobId) {
        return GithubHelper.getForLogs("/repos/" + organization + "/" + repoName + "/actions/jobs/" + jobId + "/logs");
    }

    public static Object listJobsForAWorkflowRunAttempt(String organization, String repoName, String jobId, Integer attempts) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + jobId + "/attempts/" + attempts.toString() + "/jobs");
    }

    public static Object listJobsForAWorkflowRun(String organization, String repoName, String jobId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + jobId + "/jobs");
    }


    // Workflow runs

    public static JSONObject reRunAJobFromAWorkflowRun(String organization, String repoName, String jobId) {
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/jobs/" + jobId + "/rerun", "{}");

    }

    public static Object listWorkflowRunForARepository(String organization, String repoName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/");
    }

    public static Object getAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId);
    }

    public static JSONObject deleteAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId);
    }

    public static Object getAWorkflowRunAttempt(String organization, String repoName, String runId, Integer attempt) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/attempts/" + attempt.toString() );
    }

    public static JSONObject downloadAWorkflowRunAttempt(String organization, String repoName, String runId, Integer attempt) {
        return GithubHelper.downloadFile("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/attempts/" + attempt.toString() + "/logs", "/tmp/" + runId + ".zip");
    }

    public static JSONObject cancelAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/cancel", "{}");

    }

    public static JSONObject forceCancelAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/force-cancel", "{}");

    }

    public static JSONObject downloadAWorkflowRunLogs(String organization, String repoName, String runId) {
       return GithubHelper.downloadFile("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/logs", "/tmp/" + runId + ".zip");
    }

    public static JSONObject deleteAWorkflowRunLogs(String organization, String repoName, String runId) {
        return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/logs");
    }

    public static Object getPendingDeploymentsForAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/pending_deployments");
    }

    public static JSONObject reRunAWorkflow(String organization, String repoName, String runId) {
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/rerun", "{}");
    }

    public static JSONObject reRunFailedJobsFromAWorkflowRun(String organization, String repoName, String runId) {
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/rerun-failed-jobs", "{}");
    }

    public static Object listWorkflowRunsForAWorkflow(String organization, String repoName, String workflowId) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/workflows/" + workflowId + "/runs");
    }

}
