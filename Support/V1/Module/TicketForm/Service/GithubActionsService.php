<?php
namespace Support\TicketForm\Service;


use Core\Github\Facade\GithubFacade;
use Core\Logger\Facade\LoggerFacade;
use Magento\Setup\Exception;

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


        try {
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

            $setIssueStatusResponse = GithubFacade::setIssueStatus($projectNodeId, $itemId, 'PVTSSF_lADOAkAMSM4A_Vq0zgyeNWA', 'f75ad846');
            $setIssueStatusResource = json_decode($setIssueStatusResponse->getBody(), true);

            $result['success'] = true;
            $result['code'] = 201;
            $result['data']['find_node_id_of_an_organization_resource'] = $findNodeIdOfAnOrganizationProjectResource;
            $result['data']['create_an_issue_resource'] = $createAnIssueResource;
            $result['data']['add_issue_to_project_resource'] = $createAnIssueResource;
            $result['data']['set_issue_status_resource'] = $setIssueStatusResource;


            return $result;

        } catch (\Exception $e) {
            $result['success'] = false;
            $result['code'] = 500;
            $result['message'] = "Error while creating an issue for project";
            return $result;
        }

    }

}
