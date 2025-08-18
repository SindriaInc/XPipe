<?php
namespace Pipelines\DedicatedForm\Service;


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

    public function findNodeIdOfAnOrganizationProject(string $organization, string $projectNumber): string
    {
        $response =  GithubFacade::findNodeIdOfAnOrganizationProject($organization, $projectNumber);
        return $response->getBody();
    }

    public function createIssueForProject(
        string $organization,
        string $repo,
        string $projectNumber,
        array $data
    ) : array
    {
        $findNodeIdOfAnOrganizationProjectResponse = GithubFacade::findNodeIdOfAnOrganizationProject($organization, $projectNumber);
        $findNodeIdOfAnOrganizationProjectResource = json_decode($findNodeIdOfAnOrganizationProjectResponse->getBody(), true);

        $projectNodeId = $findNodeIdOfAnOrganizationProjectResource['data']['organization']['projectV2']['id'];
        $projectName = $findNodeIdOfAnOrganizationProjectResource['data']['organization']['projectV2']['title'];

        $createAnIssueResponse = GithubFacade::createAnIssue($organization, $repo, $data['title'], $data['description']);
        $createAnIssueResource = json_decode($createAnIssueResponse->getBody(), true);

        $issueNodeId = $createAnIssueResource['node_id'];

        $addIssueToProjectResponse = GithubFacade::addIssueToProject($projectNodeId, $issueNodeId);
        $addIssueToProjectResource = json_decode($addIssueToProjectResponse->getBody(), true);
        $itemId = $addIssueToProjectResource['data']['addProjectV2ItemById']['item']['id'];
//        dd($addIssueToProjectResource);

        $setIssueStatusResponse = GithubFacade::setIssueStatus($projectNodeId, $itemId, 'PVTSSF_lADOAkAMSM4A_Vq0zgyeNWA', 'f75ad846');
        $setIssueStatusResource = json_decode($setIssueStatusResponse->getBody(), true);




        dd($setIssueStatusResource);

    }

}
