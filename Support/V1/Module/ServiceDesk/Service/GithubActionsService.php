<?php
namespace Support\ServiceDesk\Service;


use Core\Github\Facade\GithubFacade;

class GithubActionsService
{
    public function listWorkflowRunsForARepository($owner, $repo)
    {
        $response = GithubFacade::listWorkflowRunForARepository($owner, $repo);

        $resource = json_decode($response->getBody(), true);

        return $resource['workflow_runs'];
    }

    public function listOrganizationRepositories(string $owner)
    {
        $response = GithubFacade::listOrganizationRepositories($owner);
        return json_decode($response->getBody(), true);
    }

    public function cancelAWorkflowRun(string $owner, string $repo, string $runId): \Laminas\Http\Response
    {
        return GithubFacade::cancelAWorkflowRun($owner, $repo, $runId);
    }

    public function downloadJobLogsForAWorkflowRun(string $owner, string $repo, string $jobId): string
    {
        $response =  GithubFacade::downloadJobLogsForAWorkflowRun($owner, $repo, $jobId);
        return $response->getBody();
    }

}
