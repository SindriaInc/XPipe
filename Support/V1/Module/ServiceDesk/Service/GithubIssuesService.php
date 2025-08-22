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

    public function closeTicket(string $ticketId): array
    {

        try {
            $response = GithubFacade::closeIssue('SindriaInc', 'XPipe', $ticketId);
            $resource = json_decode($response->getBody(), true);

            if ($response->getStatusCode() !== 200) {
                $result = [];
                $result['success'] = false;
                $result['code'] = $response->getStatusCode();
                $result['data'] = [];
                return $result;
            }

            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;

            return $result;
        } catch (\Exception $e) {
            $result = [];
            $result['success'] = false;
            $result['code'] = 500;
            $result['data'] = [];
            return $result;
        }

    }

}
