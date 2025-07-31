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

    public function listOrganizationRepositories(string $organization) : array
    {
        $response = GithubFacade::listOrganizationRepositories($organization);
        $resource = json_decode($response->getBody(), true);

        if ($response->getStatusCode() === 404) {
            $result = [];
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = [];
            return $result;
        }

        // Organization without repositories
        if ($response->getStatusCode() === 200) {
            $result = [];
            $result['success'] = true;
            $result['code'] = 404;
            $result['data'] = [];
            return $result;
        }

        $result = [];
        $result['success'] = true;
        $result['code'] = $response->getStatusCode();
        $result['data'] = $resource;
        return $result;
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
