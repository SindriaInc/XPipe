<?php
namespace Core\Github\Facade;

use Core\Github\Builder\RepositoryBuilder;
use Laminas\Http\Client;
use Laminas\Http\Request;

use Core\Github\Helper\GithubHelper;
use Core\Github\Model\Variable;
use Core\Github\Model\Workflow;

class GithubFacade
{
    protected static function client(): GithubHelper
    {
        return GithubHelper::getInstance();
    }

    // Metodo magico per fallback automatico
    public static function __callStatic($name, $arguments)
    {
        $client = self::client();
        if (method_exists($client, $name)) {
            return call_user_func_array([$client, $name], $arguments);
        }

        throw new \BadMethodCallException("Method $name does not exist on client");
    }



    public static function listOrganizationRepositories(string $organization): \Laminas\Http\Response
    {
        $uri = "orgs/" . $organization  . "/repos";
        return self::client()->get($uri);
    }

    public static function getARepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository;
        return self::client()->get($uri);
    }


    public static function createAnOrganizationRepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $payload = (new RepositoryBuilder($repository))
            ->setDescription('A repository')
            ->setIsPrivate(false)
            ->setHasIssues(false)
            ->setTeamId(1)
            ->setLicenseTemplate("mit")
            ->build();


        $uri = "repos/" . $organization . "/repos";
        return self::client()->postRaw($uri, $payload->serialize());
    }

    public static function updateARepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $payload = (new RepositoryBuilder($repository))
            ->setDescription('A repository')
            ->setIsPrivate(false)
            ->setHasIssues(false)
            ->setTeamId(1)
            ->setLicenseTemplate("mit")
            ->build();


        $uri = "repos/" . $organization . "/repos";
        return self::client()->patchRaw($uri, $payload->serialize());
    }



    // Actions/Variables

    public static function listRepositoryVariable(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables";
        return self::client()->get($uri);
    }

    public static function createARepositoryVariable(
        string $organization,
        string $repository,
        string $variableKey,
        string $variableValue
    ): \Laminas\Http\Response
    {
        $payload = new Variable($variableKey, $variableValue);
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables";
        return self::client()->postRaw($uri, $payload->serialize());
    }

    public static function getARepositoryVariable(string $organization, string $repository, string $variableKey): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->get($uri);
    }

    public static function updateARepositoryVariable(
        string $organization,
        string $repository,
        string $variableKey,
        string $variableValue
    ): \Laminas\Http\Response
    {
        $payload = new Variable($variableKey, $variableValue);
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->patchRaw($uri, $payload->serialize());
    }

    public static function deleteARepositoryVariable(string $organization, string $repository, string $variableKey): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->delete($uri);
    }

    // Actions/Artifacts

    public static function listArtifactsForARepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/artifacts";
        return self::client()->get($uri);
    }

    public static function getAnArtifact(string $organization, string $repository, string $artifactId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/artifacts/" . $artifactId;
        return self::client()->get($uri);
    }

    public static function deleteAnArtifact(string $organization, string $repository, string $artifactId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/artifacts/" . $artifactId;
        return self::client()->delete($uri);
    }

    public static function listWorkflowRunArtifact(string $organization, string $repository, string $workflowRunId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $workflowRunId . "/artifacts";
        return self::client()->get($uri);
    }

//TODO: implement | never implemented in java
//    public static JSONObject downloadAnArtifact(String organization, String repoName, String artifactId) {
//        return GithubHelper.downloadFile("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId + "/zip", "/tmp/" + repoName.toLowerCase() + "_" + artifactId + ".zip");
//    }


    // Actions/Workflows

    public static function listRepositoryWorkflows(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows";
        return self::client()->get($uri);
    }

    public static function getAWorkflow(string $organization, string $repository, string $workflowId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows/" . $workflowId;
        return self::client()->get($uri);
    }

    public static function enableAWorkflow(string $organization, string $repository, string $workflowId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows/" . $workflowId . "/enable";
        return self::client()->put($uri);
    }

    public static function disableAWorkflow(string $organization, string $repository, string $workflowId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows/" . $workflowId . "/disable";
        return self::client()->put($uri);
    }


    public static function createAWorkflowDispatchEvent(
        string $organization,
        string $repository,
        string $workflowId,
        string $ref,
        array $inputs,
        string $variableValue
    ): \Laminas\Http\Response
    {
        $payload = new Workflow($ref, $inputs);
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows/" . $workflowId . "/dispatches";
        return self::client()->postRaw($uri, $payload->serialize());
    }

    // Actions/Workflow jobs


    public static function getAJobForAWorkflowRun(string $organization, string $repository, string $jobId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/jobs/" . $jobId;
        return self::client()->get($uri);
    }

    public static function downloadJobLogsForAWorkflowRun(string $organization, string $repository, string $jobId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/jobs/" . $jobId . "/logs";
        return self::client()->getForLogs($uri);
    }

    public static function listJobsForAWorkflowRunAttempt(string $organization, string $repository, string $jobId, int $attempts): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $jobId . "/attempts/" . $attempts . "/jobs";
        return self::client()->get($uri);
    }

    public static function listJobsForAWorkflowRun(string $organization, string $repository, string $jobId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $jobId . "/jobs";
        return self::client()->get($uri);
    }


    // Workflow runs

    public static function reRunAJobFromAWorkflowRun(string $organization, string $repository, string $jobId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/jobs/" . $jobId . "/rerun";
        return self::client()->post($uri);
    }

    public static function listWorkflowRunForARepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs";
        return self::client()->get($uri);
    }

    public static function getAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId;
        return self::client()->get($uri);
    }

    public static function deleteAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId;
        return self::client()->delete($uri);
    }

    public static function getAWorkflowRunAttempt(string $organization, string $repository, string $runId, string $attempt): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/attempts/" . $attempt;
        return self::client()->get($uri);
    }


    //TODO IMPLEMENT GET FOR LOGS with follow redirect
//    public static function downloadAWorkflowRunAttempt(string $organization, string $repository, string $runId, string $attempt): \Laminas\Http\Response
//    {
//        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/attempts/" . $attempt . "/logs";
//        return self::client()->get($uri);
//    }

    public static function cancelAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/cancel";
        return self::client()->post($uri);
    }

    public static function forceCancelAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/force-cancel";
        return self::client()->post($uri);
    }


////TODO IMPLEMENT GET FOR LOGS with follow redirect
//    public static JSONObject downloadAWorkflowRunLogs(String organization, String repoName, String runId) {
//    return GithubHelper.downloadFile("/repos/" + organization + "/" + repoName + "/actions/runs/" + runId + "/logs", "/tmp/" + runId + ".zip");
//}

    public static function deleteAWorkflowRunLogs(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/logs";
        return self::client()->delete($uri);
    }

    public static function getPendingDeploymentsForAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/pending_deployments";
        return self::client()->get($uri);
    }

    public static function reRunAWorkflow(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/rerun";
        return self::client()->post($uri);
    }

    public static function reRunFailedJobsFromAWorkflowRun(string $organization, string $repository, string $runId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/runs/" . $runId . "/rerun-failed-jobs";
        return self::client()->post($uri);
    }

    public static function listWorkflowRunsForAWorkflow(string $organization, string $repository, string $workflowId): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/workflows/" . $workflowId . "/runs";
        return self::client()->get($uri);
    }

    // Projects

    public static function findNodeIdOfAnOrganizationProject(string $organization, string $projectNumber): \Laminas\Http\Response
    {

//        {"query":"query{organization(login: \"SindriaInc\") {projectV2(number: 5){id title}}}"}
        $payload = '{"query": "query { organization(login: \"' . $organization . '\") { projectV2(number: ' . $projectNumber . ') { id title } } }"}';

        return self::client()->postGraphqlRaw($payload);

    }

    public static function createAnIssue(string $organization, string $repository, string $title, string $description,array $labels, string $issueType): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/issues";

        $payload = json_encode([
            "title" => $title,
            "body"  => $description,
            "labels" => $labels,
            "type" => $issueType,
        ]);

        return self::client()->postRaw($uri, $payload);
    }


    public static function addIssueToProject(string $projectId, string $contentId): \Laminas\Http\Response
    {

//        {"query": "mutation { addProjectV2ItemById(input: { projectId: \"PVT_kwDOAkAMSM4A_Vq0\", contentId: \"I_kwDOLgwggc7DmtPE\" }) { item { id } } }"}

        $payload = '{"query": "mutation { addProjectV2ItemById(input: { projectId: \"' . $projectId . '\", contentId: \"' . $contentId . '\" }) { item { id } } }"}';
        return self::client()->postGraphqlRaw($payload);

    }

    public static function closeIssue(string $organization, string $repository, string $issueNumber): \Laminas\Http\Response
    {
        $payload = '{"state": "closed"}';
        $uri = "repos/" . $organization . "/" . $repository . "/issues/" . $issueNumber;
        return self::client()->postRaw($uri, $payload);
    }


    public static function setIssueStatus(string $projectId, string $itemId, string $fieldId, string  $singleSelectOptionId): \Laminas\Http\Response
    {

//        {"query": "mutation { addProjectV2ItemById(input: { projectId: \"PVT_kwDOAkAMSM4A_Vq0\", contentId: \"I_kwDOLgwggc7DmtPE\" }) { item { id } } }"}

        $payload = '{"query": "mutation { updateProjectV2ItemFieldValue(input: { projectId: \"' . $projectId . '\", itemId: \"' . $itemId . '\", fieldId: \"' . $fieldId . '\", value: { singleSelectOptionId: \"' . $singleSelectOptionId . '\" } }) { projectV2Item { id } } }"}';
        return self::client()->postGraphqlRaw($payload);

    }


    public static function getIssueStatus(string $issueNodeId): \Laminas\Http\Response
    {

        $payload = [
            "query" => "query(\$id: ID!) { node(id: \$id) { ... on Issue { id title projectItems(first: 10) { nodes { id project { title } statusField: fieldValues(first: 10) { nodes { ... on ProjectV2ItemFieldSingleSelectValue { field { ... on ProjectV2SingleSelectField { name } } name } } } } } } } }",
            "variables" => [
                "id" => $issueNodeId
            ]
        ];

        return self::client()->postGraphqlRaw(json_encode($payload));

    }

    // Labels

    public static function listIssuesByLabels(string $organization, string $repository, string $labels = ''): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/issues?labels=" . $labels;
        return self::client()->get($uri);
    }

    public static function listIssueTypesForAnOrganization(string $organization): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/issue-types";
        return self::client()->get($uri);
    }


}
