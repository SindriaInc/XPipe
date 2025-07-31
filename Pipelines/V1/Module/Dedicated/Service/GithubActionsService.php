<?php
namespace Pipelines\Dedicated\Service;

use Core\Github\Facade\GithubFacade;

class GithubActionsService
{
    public function listWorkflowRunsForARepository($organization, $repo)
    {
        $response = GithubFacade::listWorkflowRunForARepository($organization, $repo);

        $resource = json_decode($response->getBody(), true);

        return $resource['workflow_runs'];
    }

    public function listOrganizationRepositories(string $organization)
    {
        $response = GithubFacade::listOrganizationRepositories($organization);

        dd($response);

        return json_decode($response->getBody(), true);
    }

    public function cancelAWorkflowRun(string $organization, string $repo, string $runId): \Laminas\Http\Response
    {
        return GithubFacade::cancelAWorkflowRun($organization, $repo, $runId);
    }

    public function downloadJobLogsForAWorkflowRun(string $organization, string $repo, string $jobId): string
    {
        $response =  GithubFacade::downloadJobLogsForAWorkflowRun($organization, $repo, $jobId);
        return $response->getBody();
    }

}
