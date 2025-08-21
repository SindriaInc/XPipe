<?php
namespace Support\ServiceDesk\Service;


use Core\Github\Facade\GithubFacade;

class GithubIssuesService
{

    public function listIssuesByOrganization(string $organization, string $repository, string $tenant = ''): array
    {

        try {
            $response = GithubFacade::listIssuesByLabels($organization, $repository, $tenant);
            $resource = json_decode($response->getBody(), true);

            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;



            return $result;
        } catch (\Exception $e) {
            return [];
        }

    }

}
