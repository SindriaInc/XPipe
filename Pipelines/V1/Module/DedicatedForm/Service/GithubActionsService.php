<?php
namespace Pipelines\DedicatedForm\Service;


use Core\Github\Facade\GithubFacade;
use Pipelines\DedicatedForm\Helper\DedicatedFormHelper;

class GithubActionsService
{
    private string $projectStatusId;
    private string $statusTriage;

    public function __construct()
    {
        $this->projectStatusId = DedicatedFormHelper::getSupportServiceDeskGitHubProjectStatusId();
        $this->statusTriage = DedicatedFormHelper::getSupportServiceDeskGitHubProjectStatusOptionTriage();
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

            $createAnIssueResponse = GithubFacade::createAnIssue($organization, $repo, $data['title'], $data['description'], ['Ticket', $data['tenant']], 'Dedicated Pipeline' );
            $createAnIssueResource = json_decode($createAnIssueResponse->getBody(), true);

            $issueNodeId = $createAnIssueResource['node_id'];

            $addIssueToProjectResponse = GithubFacade::addIssueToProject($projectNodeId, $issueNodeId);
            $addIssueToProjectResource = json_decode($addIssueToProjectResponse->getBody(), true);
            $itemId = $addIssueToProjectResource['data']['addProjectV2ItemById']['item']['id'];

            $setIssueStatusResponse = GithubFacade::setIssueStatus($projectNodeId, $itemId, $this->projectStatusId, $this->statusTriage);
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
