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

            // Project without issues
            if ($response->getStatusCode() === 200 && empty($resource)) {
                $result = [];
                $result['success'] = true;
                $result['code'] = 404;
                $result['data'] = [];
                return $result;
            }


            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;

            return $result;
        } catch (\Exception $e) {
            $result['success'] = false;
            $result['code'] = 500;
            $result['data'] = [];
            return $result;
        }

    }


    public function getTicketStatus(string $issueNodeId): array
    {
        try {
            $response = GithubFacade::getIssueStatus($issueNodeId);
            $resource = json_decode($response->getBody(), true);

            if (!isset($resource['data']['node']['projectItems']['nodes'])) {
                $result = [];
                $result['success'] = true;
                $result['code'] = 404;
                $result['data'] = [];
                return $result;
            }

            $projectItems = $resource['data']['node']['projectItems']['nodes'];

            foreach ($projectItems as $item) {
                if (!isset($item['statusField']['nodes'])) {
                    continue;
                }

                foreach ($item['statusField']['nodes'] as $fieldNode) {
                    if (isset($fieldNode['field']['name']) && $fieldNode['field']['name'] === 'Status') {
                        $result['success'] = true;
                        $result['code'] = 404;
                        $result['data'] = $fieldNode['name']; // es. "Triage", "In Progress", "Done"
                        return $result;
                    }
                }
            }

            $result['success'] = true;
            $result['code'] = 404;
            $result['data'] = [];

            return $result;

        } catch (\Exception $e) {
            $result['success'] = false;
            $result['code'] = 500;
            $result['data'] = [];
            return $result;
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
