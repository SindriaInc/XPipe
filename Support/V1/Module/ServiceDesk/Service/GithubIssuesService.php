<?php
namespace Support\ServiceDesk\Service;


use Core\Github\Facade\GithubFacade;

class GithubIssuesService
{

    public function listIssuesByOrganization(string $owner, string $repository, string $labels = ''): array
    {
        $response = GithubFacade::listIssuesByLabels($owner, $repository, $labels);
        return json_decode($response->getBody(), true);
    }

}
